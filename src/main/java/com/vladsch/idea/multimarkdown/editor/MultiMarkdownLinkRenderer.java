/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static org.pegdown.FastEncoder.obfuscate;

public class MultiMarkdownLinkRenderer extends LinkRenderer {
    protected Project project;
    protected Document document;
    protected String missingTargetClass;

    public MultiMarkdownLinkRenderer() {
        super();
        project = null;
        document = null;
        missingTargetClass = null;
    }

    public MultiMarkdownLinkRenderer(Project project, Document document, String missingTargetClass) {
        super();
        this.project = project;
        this.document = document;
        this.missingTargetClass = missingTargetClass;
    }

    // TODO: need to implement this using ProjectComponent methods so that we don't need
    // to go into areas that may have threading issues.
    public Rendering checkTarget(Rendering rendering) {
        if (project != null && document != null && missingTargetClass != null) {

            // test if it isn't internet protocol and mailto:, the rest we'll handle in the project file system
            //MultiMarkdownPathResolver.canResolveLink(project, document, rendering.href);
            String href = rendering.href;
            boolean linkFound = (!href.startsWith("http://") && !href.startsWith("ftp://") && !href.startsWith("https://") && !href.startsWith("mailto:"));

            if (!linkFound) {
                // see if we can handle it
                VirtualFile virtualTarget = null;

                if (href.startsWith("file:")) {
                    try {
                        URL target = new URL(href);
                        VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
                        virtualTarget = virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(target.getFile());
                    } catch (MalformedURLException e) {
                        //e.printStackTrace();
                    }
                }

                if (virtualTarget == null || !virtualTarget.exists()) {
                    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                    VirtualFile parent = file == null ? null : file.getParent();
                    virtualTarget = parent == null ? null : parent.findFileByRelativePath(href);
                }

                linkFound = virtualTarget != null;
            }

            if (!linkFound) {
                rendering.withAttribute("class", missingTargetClass);
            }
        }
        return rendering;
    }

    @Override public Rendering render(AnchorLinkNode node) {
        return checkTarget(super.render(node));
    }

    @Override public Rendering render(AutoLinkNode node) {
        return checkTarget(super.render(node));
    }

    @Override public Rendering render(ExpLinkNode node, String text) {
        return checkTarget(super.render(node, text));
    }

    @Override public Rendering render(ExpImageNode node, String text) {
        return checkTarget(super.render(node, text));
    }

    @Override public Rendering render(RefLinkNode node, String url, String title, String text) {
        return checkTarget(super.render(node, url, title, text));
    }

    @Override public Rendering render(RefImageNode node, String url, String title, String alt) {
        return checkTarget(super.render(node, url, title, alt));
    }

    @Override
    public Rendering render(MailLinkNode node) {
        String obfuscated = obfuscate(node.getText());
        return (new Rendering(obfuscate("mailto:") + obfuscated, obfuscated)).withAttribute("class", obfuscate("mail-link"));
    }

    @Override
    public Rendering render(WikiLinkNode node) {
        if (project == null || document == null || missingTargetClass == null) {
            return checkTarget(super.render(node));
        }
        try {
            // vsch: #182 handle WikiLinks alternative format [[page|text]]
            String text = node.getText();
            String url = text;
            int pos;
            if ((pos = text.indexOf("|")) >= 0) {
                url = text.substring(0, pos);
                text = text.substring(pos + 1);
            }

            // vsch: need our own extension for the file
            url = "./" + URLEncoder.encode(url.replace(' ', '-'), "UTF-8") + ".md";
            return checkTarget(new Rendering(url, text));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }
}
