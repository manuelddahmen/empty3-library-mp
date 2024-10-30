package one.empty3.library1.tree

import one.empty3.library1.tree.StringAnalyzerJava2.TokenExpression2

class TokenExpression2toString {
    public fun toString(s: String): String {
        val sb: StringBuilder = StringBuilder(s)
        val stringAnalyzerJava2: StringAnalyzerJava2 = StringAnalyzerJava2()
        var result = true
        var pos = 0
        var hasNextToken = true
        while (hasNextToken) {
            var tokenExpression2: StringAnalyzerJava2.TokenExpression2 = stringAnalyzerJava2.TokenExpression2()
            var premier = true
            while (pos < s.length - 1 && result) {
                if (!premier)
                    sb.append(" ")
                tokenExpression2 = stringAnalyzerJava2.TokenExpression2()
                pos = tokenExpression2.parse(s, pos)
                result = tokenExpression2.isSuccessful

                var tokenString = ""
                if (tokenExpression2.toString().length > 0) {
                    tokenString = tokenExpression2.toString().substring(1)
                    if (tokenString.length >= 1 && tokenString.contains(';')) {
                        tokenString = tokenString.replace(";", ";\n")
                    }
                }
                premier = false
                sb.append(tokenString.toString() + " ")

            }
            if (tokenExpression2.hasNextToken()) {
                hasNextToken = true
                tokenExpression2 = tokenExpression2.nextToken as TokenExpression2

            } else {
                break
            }
        }
        return sb.toString()
    }

    fun toString(tokenExpression2: StringAnalyzerJava2.TokenExpression2): String {
        var expression2 = tokenExpression2
        var result = true
        var pos = 0
        var hasNextToken = true
        val sb = StringBuilder()
        while (hasNextToken) {
            var premier = true
            if (!premier)
                sb.append(" ")
            var tokenString = ""
            if (expression2.toString().length > 0) {
                tokenString = expression2.toString().substring(1)
                if (tokenString.length >= 1 && tokenString.contains(';')) {
                    tokenString = tokenString.replace(";", ";\n")
                }
            }
            premier = false
            sb.append(tokenString).append(" ")

            if (expression2.hasNextToken()) {
                hasNextToken = true
                expression2 = expression2.nextToken as TokenExpression2

            } else
                hasNextToken = false
        }
        return sb.toString()

    }
}

