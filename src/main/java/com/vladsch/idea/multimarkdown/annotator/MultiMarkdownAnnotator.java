/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.*;

//public class MultiMarkdownAnnotator extends ExternalAnnotator<String, Set<MultiMarkdownAnnotator.HighlightableToken>> {
public class MultiMarkdownAnnotator implements Annotator {
    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownAnnotator.class);

    //private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new MultiMarkdownSyntaxHighlighter();
    //private MultiMarkdownGlobalSettingsListener globalSettingsListener = null;

    ///** Build a new instance of {@link MultiMarkdownAnnotator}. */
    //public MultiMarkdownAnnotator() {
    //    // Listen to global settings changes.
    //    MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
    //        public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
    //        }
    //    });
    //}

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof MultiMarkdownWikiPageRef) {
            // see if it exists
            int searchSettings = WIKI_REF;

            MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();
            VirtualFile virtualFile = containingFile.getVirtualFile();

            boolean isWikiPage = containingFile.isWikiPage();
            searchSettings |= isWikiPage ? WIKIPAGE_FILE : MARKDOWN_FILE;

            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
            List<MultiMarkdownFile> wikiFiles = projectComponent.findRefLinkMarkdownFiles(((MultiMarkdownWikiPageRef) element).getName(), virtualFile, searchSettings);

            Annotation annotator = null;

            if (wikiFiles != null && wikiFiles.size() == 1) {
                //String path = wikiFiles.get(0).getVirtualFile().getPath();
                //annotator = holder.createInfoAnnotation(element.getTextRange(), " ");
                //annotator.setTooltip(path);
            } else {
                // not set to right name or to an accessible name
                // get all accessibles
                wikiFiles = projectComponent.findRefLinkMarkdownFiles(((MultiMarkdownWikiPageRef) element).getName(), virtualFile, searchSettings | ALLOW_INACCESSIBLE_WIKI_REF);

                boolean linkInAccessible = wikiFiles != null && wikiFiles.size() == 1;

                // get all accessibles
                wikiFiles = projectComponent.findRefLinkMarkdownFiles(null, virtualFile, searchSettings);


                if (linkInAccessible) {
                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                            MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference"));
                } else {
                    // can offer to move the file, just add the logic
                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                            MultiMarkdownBundle.message("annotation.wikilink.unresolved-page-reference"));

                    // allow creation fix
                    // TODO: validate file name before creating a quitck fix for it
                    String fileName = ((MultiMarkdownWikiPageRef) element).getFileName();
                    //VirtualFileSystem fileSystem = element.getContainingFile().getVirtualFile().getFileSystem();
                    annotator.registerFix(new CreateWikiPageQuickFix(fileName));
                }

                annotator.setNeedsUpdateOnTyping(true);

                if (wikiFiles != null && wikiFiles.size() != 0) {
                    /*
                     *   have a file but it is not accessible we can:
                     *   1. rename the link to another accessible file?
                     */

                    for (MultiMarkdownFile file : wikiFiles) {
                        String wikiPageRef = file.getWikiPageRef(virtualFile);
                        if (wikiPageRef != null) {
                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, wikiPageRef));
                        }
                    }
                }

                if (false && !isWikiPage) {
                    List<MultiMarkdownFile> markdownFiles = projectComponent.findRefLinkMarkdownFiles(((MultiMarkdownWikiPageRef) element).getName(), virtualFile, (searchSettings & ~WIKIPAGE_FILE) | ALLOW_INACCESSIBLE_WIKI_REF);

                    if (markdownFiles != null && markdownFiles.size() != 0) {
                    /*
                     *   have files but they are not accessible we can:
                     *   1. move file
                     */
                        // TODO: create MoveFileQuickFix
                    }
                }
            }
        }
    }

///**
// * Get the text source of the given file.
// *
// * @param file the {@link PsiFile} to process.
// *
// * @return the file text.
// */
//@Nullable @Override
//public String collectInformation(@NotNull PsiFile file) {
//    return file.getText();
//}
//
///**
// * Collect {@link MultiMarkdownAnnotator.HighlightableToken}s from the given file.
// *
// * @param source the source text to process.
// *
// * @return a {@link Set} of {@link MultiMarkdownAnnotator.HighlightableToken}s that should be used to do the file syntax highlighting.
// */
//@Override
//public Set<HighlightableToken> doAnnotate(final String source) {
//    return new HashSet<HighlightableToken>();
//}
//
///**
// * Convert collected {@link MultiMarkdownAnnotator.HighlightableToken}s in syntax highlighting annotations.
// *
// * @param file             the source file.
// * @param annotationResult the {@link Set} of {@link MultiMarkdownAnnotator.HighlightableToken}s collected on the file.
// * @param holder           the annotation holder.
// */
//@Override
//public void apply(final @NotNull PsiFile file,
//        final Set<HighlightableToken> annotationResult,
//        final @NotNull AnnotationHolder holder) {
//
//    for (final HighlightableToken token : annotationResult) {
//        final TextAttributesKey[] attrs = SYNTAX_HIGHLIGHTER.getTokenHighlights(token.getElementType());
//
//        if (attrs.length > 0) holder.createInfoAnnotation(token.getRange(), null).setTextAttributes(attrs[0]);
//    }
//}
//
//protected class HighlightableToken {
//
//    protected final TextRange range;
//    protected final IElementType elementType;
//
//    public HighlightableToken(final TextRange range, final IElementType elementType) {
//        this.range = range;
//        this.elementType = elementType;
//    }
//
//    public TextRange getRange() { return range; }
//
//    public IElementType getElementType() { return elementType; }
//}
}
