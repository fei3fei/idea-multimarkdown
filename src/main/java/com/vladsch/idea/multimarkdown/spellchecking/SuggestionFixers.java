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
 */
package com.vladsch.idea.multimarkdown.spellchecking;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.spellchecker.SpellCheckerManager;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionFixers {
    public static final CleanSpacedWordsFixer SuggestCleanSpacedWords = new CleanSpacedWordsFixer();
    public static final CapSpacedWordsFixer SuggestCapSpacedWords = new CapSpacedWordsFixer();
    public static final LowerSpacedWordsFixer SuggestLowerSpacedWords = new LowerSpacedWordsFixer();
    public static final UpperSpacedWordsFixer SuggestUpperSpacedWords = new UpperSpacedWordsFixer();
    public static final CleanSplicedWordsFixer SuggestCleanSplicedWords = new CleanSplicedWordsFixer();
    public static final CapSplicedWordsFixer SuggestCapSplicedWords = new CapSplicedWordsFixer();
    public static final LowerSplicedWordsFixer SuggestLowerSplicedWords = new LowerSplicedWordsFixer();
    public static final UpperSplicedWordsFixer SuggestUpperSplicedWords = new UpperSplicedWordsFixer();
    public static final CleanDashedWordsFixer SuggestCleanDashedWords = new CleanDashedWordsFixer();
    public static final CapDashedWordsFixer SuggestCapDashedWords = new CapDashedWordsFixer();
    public static final LowerDashedWordsFixer SuggestLowerDashedWords = new LowerDashedWordsFixer();
    public static final UpperDashedWordsFixer SuggestUpperDashedWords = new UpperDashedWordsFixer();
    public static final CleanSnakedWordsFixer SuggestCleanSnakedWords = new CleanSnakedWordsFixer();
    public static final CapSnakedWordsFixer SuggestCapSnakedWords = new CapSnakedWordsFixer();
    public static final LowerSnakedWordsFixer SuggestLowerSnakedWords = new LowerSnakedWordsFixer();
    public static final UpperSnakedWordsFixer SuggestUpperSnakedWords = new UpperSnakedWordsFixer();
    public static final WikiRefAsFilNameWithExtFixer SuggestWikiRefAsFilNameWithExt = new WikiRefAsFilNameWithExtFixer();
    public static final SpellingFixer SuggestSpelling = new SpellingFixer();

    public static class WikiRefAsFilNameWithExtFixer implements SuggestionList.SuggestionFixer {
        @Nullable
        @Override
        public SuggestionList fix(@NotNull Suggestion suggestion, Project project) {
            SuggestionList suggestionList = new SuggestionList();
            String wikiRef = FilePathInfo.wikiRefAsFileNameNoExt(suggestion.getText());
            suggestionList.add(wikiRef + FilePathInfo.WIKI_PAGE_EXTENSION, suggestion);
            return suggestionList;
        }
    }

    public static class SpellingFixer implements SuggestionList.SuggestionFixer {
        final public static String NEEDS_SPELLING_FIXER = "NeedsSpellingFixer";
        final public static String HAD_SPELLING_FIXER = "HadSpellingFixer";

        @Nullable
        @Override
        public SuggestionList fix(@NotNull Suggestion suggestion, Project project) {
            if (!suggestion.boolParam(NEEDS_SPELLING_FIXER) || suggestion.boolParam(HAD_SPELLING_FIXER)) return null;

            SuggestionList suggestionList = new SuggestionList();
            SpellCheckerManager manager = SpellCheckerManager.getInstance(project);
            List<String> spellingSuggestions = getSuggestions(manager, suggestion.getText());
            Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(HAD_SPELLING_FIXER, true);
            for (String text : spellingSuggestions) {
                suggestionList.add(text, param, suggestion);
            }
            return suggestionList;
        }
    }

    public static abstract class WordsFixerBase implements SuggestionList.SuggestionFixer {
        private Suggestion.Param<Boolean> param;
        private SuggestionList suggestionList;
        private Suggestion sourceSuggestion;

        @Nullable
        @Override
        public SuggestionList fix(@NotNull Suggestion suggestion, @NotNull Project project) {
            SuggestionList suggestedNames = new SuggestionList();

            String[] words = suggestion.getWords();
            String cleanedSuggestion = "";
            boolean needSpellingSuggestions = false;
            boolean prevWasAlphaNum = false;

            SpellCheckerManager manager = SpellCheckerManager.getInstance(project);

            for (String word : words) {
                boolean isAlphaNum = isAlphaNum(word);

                if (manager.hasProblem(word)) needSpellingSuggestions = true;
                if (isAlphaNum && prevWasAlphaNum) {
                    cleanedSuggestion += getWordSpacer();
                }
                cleanedSuggestion += fixWord(word);
                prevWasAlphaNum = isAlphaNum;
            }

            param = new Suggestion.Param<Boolean>(SpellingFixer.NEEDS_SPELLING_FIXER, true);
            suggestionList = suggestedNames;
            sourceSuggestion = suggestion;

            cleanedSuggestion = fixSuggestion(cleanedSuggestion, " -_.'/\\", getWordSpacer());
            makeSuggestions(cleanedSuggestion);
            return suggestedNames;
        }

        protected final void addSuggestion(@NotNull Suggestion suggestion) {
            suggestionList.add(suggestion.getText(), param, sourceSuggestion, suggestion);
        }

        protected final void addSuggestion(@NotNull String suggestion) {
            suggestionList.add(suggestion, param, sourceSuggestion);
        }

        @NotNull
        public String getWordSpacer() {
            return " ";
        }

        @NotNull
        public String fixWord(@NotNull String word) {
            return word;
        }

        public abstract void makeSuggestions(@NotNull String cleanedWord);
    }

    public static class CleanSpacedWordsFixer extends WordsFixerBase {
        @Override
        public void makeSuggestions(@NotNull String cleanedWord) {
            addSuggestion(new Suggestion(cleanedWord));
        }
    }

    public static class CapSpacedWordsFixer extends WordsFixerBase {
        @NotNull
        @Override
        public String fixWord(@NotNull String word) {
            return StringUtil.capitalize(word.toLowerCase());
        }

        @Override
        public void makeSuggestions(@NotNull String cleanedWord) {
            addSuggestion(cleanedWord);
        }
    }

    public static class LowerSpacedWordsFixer extends WordsFixerBase {
        @NotNull
        @Override
        public String fixWord(@NotNull String word) {
            return word.toLowerCase();
        }

        @Override
        public void makeSuggestions(@NotNull String cleanedWord) {
            addSuggestion(cleanedWord);
        }
    }

    public static class UpperSpacedWordsFixer extends WordsFixerBase {
        @NotNull
        @Override
        public String fixWord(@NotNull String word) {
            return word.toUpperCase();
        }

        @Override
        public void makeSuggestions(@NotNull String cleanedWord) {
            addSuggestion(cleanedWord);
        }
    }

    public static class CleanSplicedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class CapSplicedWordsFixer extends CapSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class LowerSplicedWordsFixer extends LowerSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class UpperSplicedWordsFixer extends UpperSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class CleanDashedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class CapDashedWordsFixer extends CapSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class LowerDashedWordsFixer extends LowerSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class UpperDashedWordsFixer extends UpperSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class CleanSnakedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static class CapSnakedWordsFixer extends CapSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static class LowerSnakedWordsFixer extends LowerSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static class UpperSnakedWordsFixer extends UpperSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static boolean isAlphaNum(@NotNull String word) {
        int iMax = word.length();
        for (int i = 0; i < iMax; i++) {
            char c = word.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static String fixSuggestion(@NotNull String suggestion, @NotNull String remove, @NotNull String pad) {// replace all unacceptables with a space
        int iMax = suggestion.length();
        remove += pad;
        StringBuilder newSuggestion = new StringBuilder(suggestion.length());
        for (int i = 0; i < iMax; i++) {
            if (remove.indexOf(suggestion.charAt(i)) >= 0) {
                if (newSuggestion.length() > 0 && newSuggestion.charAt(newSuggestion.length() - 1) != ' ') {
                    newSuggestion.append(pad);
                }
                continue;
            }
            newSuggestion.append(suggestion.charAt(i));
        }
        suggestion = newSuggestion.toString();
        return suggestion;
    }

    @NotNull
    public static List<String> getSuggestions(SpellCheckerManager manager, @NotNull String text) {

        String[] words = NameUtil.nameToWords(text);

        int index = 0;
        List[] res = new List[words.length];
        int i = 0;
        for (String word : words) {
            int start = text.indexOf(word, index);
            int end = start + word.length();
            if (manager.hasProblem(word)) {
                List<String> variants = manager.getSuggestions(word);
                res[i++] = variants;
            } else {
                List<String> variants = new ArrayList<String>();
                variants.add(word);
                res[i++] = variants;
            }
            index = end;
        }

        int[] counter = new int[i];
        int size = 1;
        for (int j = 0; j < i; j++) {
            size *= res[j].size();
        }
        String[] all = new String[size];

        for (int k = 0; k < size; k++) {
            boolean prevAlnum = false;

            for (int j = 0; j < i; j++) {
                boolean isAlnum = isAlphaNum((String) res[j].get(counter[j]));

                if (all[k] == null) {
                    all[k] = "";
                } else if (isAlnum && prevAlnum) {
                    all[k] += " ";
                }

                all[k] += res[j].get(counter[j]);
                prevAlnum = isAlnum;

                counter[j]++;
                if (counter[j] >= res[j].size()) {
                    counter[j] = 0;
                }
            }
        }

        return Arrays.asList(all);
    }
}