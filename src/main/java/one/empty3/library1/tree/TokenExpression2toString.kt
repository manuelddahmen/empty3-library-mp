package one.empty3.library1.tree

import one.empty3.library1.tree.StringAnalyzerJava2.TokenExpression2

class TokenExpression2toString {
    public fun toString(token: TokenExpression2): String {
        var result: Boolean = true
        var pos: Int = 0
        var hasNextToken: Boolean = true
        var s: String = ""
        var stringAnalyzer3: StringAnalyzerJava2 = StringAnalyzerJava2()
        val sb = StringBuilder()
        while (hasNextToken) {
            var tokenExpression2: StringAnalyzerJava2.TokenExpression2 = token
            var premier = true
            while (pos < s.length - 1 && result) {
                if (!premier)
                    sb.append(" ")
                tokenExpression2 = stringAnalyzer3.TokenExpression2()
                pos = tokenExpression2.parse(s, pos)
                result = tokenExpression2.isSuccessful

                var tokenString = ""
                if (tokenExpression2.toString().length > 0) {
                    tokenString = tokenExpression2.toString().substring(1)
                    if (tokenString.length >= 1 && tokenString.contains(';')) {
                        sb.append(tokenString.replace(";", ";\n"))
                    }
                }
                premier = false
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

    fun toString(s: String, tokenExpression2p: StringAnalyzerJava1.TokenExpression2): String {
        var result: Boolean = true
        var pos: Int = 0
        var hasNextToken: Boolean = true
        var stringAnalyzer3: StringAnalyzerJava2 = StringAnalyzerJava2()
        val sb = StringBuilder()
        var tokenExpression2 = tokenExpression2p
        while (hasNextToken) {
            var premier = true
            while (pos < s.length - 1 && result) {
                if (!premier)
                    sb.append(" ")
                pos = tokenExpression2.parse(s, pos)
                result = tokenExpression2.isSuccessful

                var tokenString = ""
                if (tokenExpression2.toString().length > 0) {
                    tokenString = tokenExpression2.toString().substring(1)
                    if (tokenString.length >= 1 && tokenString.contains(';')) {
                        sb.append(tokenString.replace(";", ";\n"))
                    }
                }
                premier = false
            }
            if (tokenExpression2.hasNextToken()) {
                hasNextToken = true
                tokenExpression2 = tokenExpression2.nextToken.getElem() as StringAnalyzerJava1.TokenExpression2
            } else {
                break
            }
        }
        return sb.toString()

    }
}

