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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

package one.empty3.library1.tree;

public class FormulaSimplifier {

    public static boolean isZero(String s) {
        if (s == null) return false;
        String trimmed = s.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = removeRedundantOuterParentheses(trimmed);
        }
        if (trimmed.equals("0") || trimmed.equals("0.0") || trimmed.equals("+0") || trimmed.equals("+0.0") ||
                trimmed.equals("-0") || trimmed.equals("-0.0") || trimmed.equals(".0") || trimmed.equals("0.")) {
            return true;
        }
        try {
            double d = Double.parseDouble(trimmed);
            return d == 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isOne(String s) {
        if (s == null) return false;
        String trimmed = s.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = removeRedundantOuterParentheses(trimmed);
        }
        if (trimmed.equals("1") || trimmed.equals("1.0") || trimmed.equals("+1") || trimmed.equals("+1.0")) {
            return true;
        }
        try {
            double d = Double.parseDouble(trimmed);
            return d == 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNegativeOne(String s) {
        if (s == null) return false;
        String trimmed = s.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = removeRedundantOuterParentheses(trimmed);
        }
        if (trimmed.equals("-1") || trimmed.equals("-1.0")) {
            return true;
        }
        try {
            double d = Double.parseDouble(trimmed);
            return d == -1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isConstant(String s) {
        if (s == null) return false;
        String trimmed = removeRedundantOuterParentheses(s.trim());
        try {
            Double.parseDouble(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double getConstantValue(String s) {
        String trimmed = removeRedundantOuterParentheses(s.trim());
        return Double.parseDouble(trimmed);
    }

    public static String formatDouble(double d) {
        if (d == (long) d) {
            return String.format("%d.0", (long) d);
        } else {
            return Double.toString(d);
        }
    }

    public static String simplifyProduct(String a, String b) {
        if (a == null || b == null) return "0.0";
        a = a.trim();
        b = b.trim();
        if (isZero(a) || isZero(b)) {
            return "0.0";
        }
        if (isOne(a)) {
            return b;
        }
        if (isOne(b)) {
            return a;
        }
        if (isNegativeOne(a)) {
            return simplifyNegation(b);
        }
        if (isNegativeOne(b)) {
            return simplifyNegation(a);
        }
        if (isConstant(a) && isConstant(b)) {
            return formatDouble(getConstantValue(a) * getConstantValue(b));
        }
        return "(" + a + " * " + b + ")";
    }

    public static String simplifyQuotient(String a, String b) {
        if (a == null || b == null) return "0.0";
        a = a.trim();
        b = b.trim();
        if (isZero(a)) {
            return "0.0";
        }
        if (isOne(b)) {
            return a;
        }
        if (isNegativeOne(b)) {
            return simplifyNegation(a);
        }
        if (isConstant(a) && isConstant(b)) {
            double valB = getConstantValue(b);
            if (valB != 0.0) {
                return formatDouble(getConstantValue(a) / valB);
            }
        }
        return "(" + a + " / " + b + ")";
    }

    public static String simplifySum(String a, String b) {
        if (a == null && b == null) return "0.0";
        if (a == null || isZero(a)) return b != null ? b.trim() : "0.0";
        if (b == null || isZero(b)) return a.trim();
        a = a.trim();
        b = b.trim();
        if (isConstant(a) && isConstant(b)) {
            return formatDouble(getConstantValue(a) + getConstantValue(b));
        }
        return "(" + a + " + " + b + ")";
    }

    public static String simplifyDifference(String a, String b) {
        if (a == null && b == null) return "0.0";
        if (b == null || isZero(b)) return a != null ? a.trim() : "0.0";
        if (a == null || isZero(a)) return simplifyNegation(b);
        a = a.trim();
        b = b.trim();
        if (isConstant(a) && isConstant(b)) {
            return formatDouble(getConstantValue(a) - getConstantValue(b));
        }
        return "(" + a + " - " + b + ")";
    }

    public static String simplifyPower(String base, String exp) {
        if (base == null || exp == null) return "0.0";
        base = base.trim();
        exp = exp.trim();
        if (isZero(exp)) {
            return "1.0";
        }
        if (isOne(exp)) {
            return base;
        }
        if (isZero(base)) {
            return "0.0";
        }
        if (isOne(base)) {
            return "1.0";
        }
        if (isConstant(base) && isConstant(exp)) {
            return formatDouble(Math.pow(getConstantValue(base), getConstantValue(exp)));
        }
        if (exp.startsWith("-")) {
            exp = "(" + exp + ")";
        }
        return "(" + base + " ^ " + exp + ")";
    }

    public static String simplifyNegation(String a) {
        if (a == null || isZero(a)) return "0.0";
        a = a.trim();
        if (isConstant(a)) {
            return formatDouble(-getConstantValue(a));
        }
        String unp = removeRedundantOuterParentheses(a);
        if (unp.startsWith("-")) {
            return unp.substring(1).trim();
        }
        return "(-" + a + ")";
    }

    public static String removeRedundantOuterParentheses(String s) {
        if (s == null) return "";
        s = s.trim();
        while (s.startsWith("(") && s.endsWith(")")) {
            // Verify if the outer pair corresponds to each other
            int count = 0;
            boolean matched = true;
            for (int i = 0; i < s.length() - 1; i++) {
                if (s.charAt(i) == '(') count++;
                else if (s.charAt(i) == ')') count--;
                if (count == 0) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                s = s.substring(1, s.length() - 1).trim();
            } else {
                break;
            }
        }
        return s;
    }

    public static String simplify(String formula) {
        if (formula == null || formula.isBlank()) {
            return "0.0";
        }
        formula = formula.replace(" ", "");
        formula = removeRedundantOuterParentheses(formula);
        if (formula.isEmpty()) {
            return "0.0";
        }
        return formula;
    }
}
