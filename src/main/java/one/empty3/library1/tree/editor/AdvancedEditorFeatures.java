/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3.library1.tree.editor;

import one.empty3.library1.tree.ListInstructions;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedEditorFeatures {

    private static final Set<String> KEYWORDS = Set.of(
        "sin", "cos", "tan", "asin", "acos", "atan", "sqrt", "cbrt", 
        "exp", "log", "pow", "atan2", "sum", "product", "avg", "median", "count"
    );

    private static boolean highlighting = false;

    public static void applySyntaxHighlighting(JTextPane textPane) {
        StyledDocument doc = textPane.getStyledDocument();

        Style keywordStyle = textPane.addStyle("Keyword", null);
        StyleConstants.setForeground(keywordStyle, new Color(0, 50, 200));
        StyleConstants.setBold(keywordStyle, true);

        Style numberStyle = textPane.addStyle("Number", null);
        StyleConstants.setForeground(numberStyle, new Color(0, 150, 0));

        Style defaultStyle = textPane.addStyle("Default", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        StyleConstants.setFontSize(defaultStyle, 14);

        doc.addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { triggerHighlight(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { triggerHighlight(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { triggerHighlight(); }

            private void triggerHighlight() {
                if (highlighting) return;
                SwingUtilities.invokeLater(() -> {
                    highlighting = true;
                    try {
                        String text = doc.getText(0, doc.getLength());
                        doc.setCharacterAttributes(0, text.length(), defaultStyle, true);

                        // Highlight keywords
                        for (String keyword : KEYWORDS) {
                            Pattern p = Pattern.compile("\\b" + keyword + "\\b");
                            Matcher m = p.matcher(text);
                            while (m.find()) {
                                doc.setCharacterAttributes(m.start(), m.end() - m.start(), keywordStyle, false);
                            }
                        }

                        // Highlight numbers
                        Pattern numP = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
                        Matcher numM = numP.matcher(text);
                        while (numM.find()) {
                            doc.setCharacterAttributes(numM.start(), numM.end() - numM.start(), numberStyle, false);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        highlighting = false;
                    }
                });
            }
        });
    }

    public static String checkSyntax(String scriptContent) {
        if (scriptContent == null || scriptContent.trim().isEmpty()) {
            return "Empty script.";
        }
        try {
            ListInstructions li = new ListInstructions();
            li.addInstructions(scriptContent);
            return "Syntax OK.";
        } catch (Exception e) {
            return "Syntax Error: " + e.getMessage();
        }
    }
}