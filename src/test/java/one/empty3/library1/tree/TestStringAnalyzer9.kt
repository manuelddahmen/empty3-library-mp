/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.library1.tree;

import one.empty3.library1.tree.StringAnalyzer3.Token
import one.empty3.library1.tree.StringAnalyzer3.TokenCloseParenthesized
import one.empty3.library1.tree.StringAnalyzerJava2.TokenExpression2
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList
import kotlin.Array

/**
 * Test class for AlgebraicTree.
 */
@RunWith(JUnit4::class)
class TestStringAnalyzer9 {
    private val isDebug: Boolean = false
    fun readDir(directory_path: String) {
        var succeed = true
        File(directory_path).listFiles()?.forEach {
            val file = it
            if (file != null && file.name.endsWith(".java_code")) {
                val stringAnalyzerJava2: StringAnalyzerJava2 = StringAnalyzerJava2()
                val javaToken8 = getJavaToken9(
                    stringAnalyzerJava2
                )
                val readString = readString(file.absolutePath)
                var parse = 0
                try {
                    parse = javaToken8.parse(readString, parse)
                } catch (ex: RuntimeException) {
                    ex.printStackTrace()
                    if (stringAnalyzerJava2.mPosition < readString.length - 1) {
                        println("Error: Parsing text not finished")
                    }
                }

                println("------------------------------------------------------------------------")
                println("- " + file.name)
                println("------------------------------------------------------------------------")
                println(readString)
                println("------------------------------------------------------------------------")
                println("- " + "amount of code: " + parse + "-" + (stringAnalyzerJava2.mPosition + 1) + "/" + readString.length)
                println("------------------------------------------------------------------------")
                //println("- " + "errors (characters remainers): ")
                //println("------------------------------------------------------------------------")
                /*System.err.println(
                    readString.substring(
                        StringAnalyzerJava2.mPosition
                    )
                );*/
                println("------------------------------------------------------------------------")
                System.err.println(
                    stringAnalyzerJava2.construct.toLangStringJava(false)
                );
                println("------------------------------------------------------------------------")


                succeed = succeed && (stringAnalyzerJava2.mPosition + 2 >= readString.length)/*&&
                compareStrings(
                    readString, stringAnalyzer3.construct.toLangStringJava(false),
                    false
                )*/

            }
        }
        if (!succeed) fail()
        else Assert.assertTrue(succeed)
    }

    /*
        fun compareStrings(s1: String, s2: String, isEchoing: Boolean): Boolean {
            val formattedSource1: String = com.google.googlejavaformat.java.Formatter().formatSource(s1)
            val formattedSource2: String = com.google.googlejavaformat.java.Formatter().formatSource(s2)

            println("---------------- ORIGINAL -------------------")
            println(formattedSource1)
            println("------------- RECONSTRUCTED -----------------")
            println(formattedSource2)

            return formattedSource1.equals(formattedSource2)
            //assertTrue(formattedSource1.equals(formattedSource2))
        }
    */
    fun readString(file_path: String): String {
        try {
            val allLines = Files.readAllLines(Paths.get(file_path))
            // Convert the List of strings to a single string
            val sb: StringBuilder = StringBuilder()
            allLines.forEach { s -> sb.append(s).append("\n") }
            return sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        throw RuntimeException("Not found or read fails")
    }

    fun getJavaToken9(
        stringAnalyzer3: StringAnalyzerJava2,
    ): StringAnalyzer3.Token {
        val tokenPackage = stringAnalyzer3.TokenPackage()
        val tokenPackageName = stringAnalyzer3.TokenName()
        val tokenPackageSemicolon = stringAnalyzer3.TokenSemiColon()
        val tokenClass: StringAnalyzer3.Token = stringAnalyzer3.TokenClassKeyword()


        class ActionPackageName(token: StringAnalyzer3.Token?) : Action3(token) {
            public override fun action(): Boolean {
                if (tokenPackageName.name.isNotEmpty()) {
                    stringAnalyzer3.construct.currentClass.setPackageName(tokenPackageName.name)
                    stringAnalyzer3.construct.packageName = tokenPackageName.name
                }
                return true
            }
        }
        ActionPackageName(tokenPackageName)

        val tokenImport = stringAnalyzer3.TokenString("import")
        val tokenImportName = stringAnalyzer3.TokenQualifiedName()
        val tokenImportSemicolon = stringAnalyzer3.TokenSemiColon()


        class ActionClassKeyword(token: StringAnalyzer3.Token?) : Action3(token) {
            public override fun action(): Boolean {
                if (stringAnalyzer3.construct.classesWithName(stringAnalyzer3.construct.currentClass.name).size == 0) {
                    stringAnalyzer3.construct.classes.add(stringAnalyzer3.construct.currentClass)
                    stringAnalyzer3.construct.currentClass.setPackageName(stringAnalyzer3.construct.packageName)
                }
                return true
            }
        }
        ActionClassKeyword(tokenClass)

        val tokenClassName = stringAnalyzer3.TokenName()

        class ActionClassname(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                if (tokenClassName.name != null) {
                    stringAnalyzer3.construct.currentClass.name = tokenClassName.name
                }
                return true
            }
        }
        ActionClassname(tokenClassName)

        val tokenOpenBracketClass = stringAnalyzer3.TokenOpenBracket()
        val tokenCloseBracketClass = stringAnalyzer3.SingleTokenOptional(stringAnalyzer3.TokenCloseBracket())

        // Variables members declarations
        val tokenMemberVarType1 = stringAnalyzer3.TokenName()
        val tokenMemberVarName1 = stringAnalyzer3.TokenName()
        val tokenMemberVarEquals1 = stringAnalyzer3.TokenEquals()
        val tokenMemberExpression1 = stringAnalyzer3.TokenExpression1()
        val tokenMemberVarSemiColon1 = stringAnalyzer3.TokenSemiColon()

        val tokenMemberVarType2 = stringAnalyzer3.TokenName()
        val tokenMemberVarName2 = stringAnalyzer3.TokenName()
        val tokenMemberVarSemiColon2 = stringAnalyzer3.TokenSemiColon()

        tokenMemberVarType1.addToken(tokenMemberVarName1)
        tokenMemberVarName1.addToken(tokenMemberVarEquals1)
        tokenMemberVarEquals1.addToken(tokenMemberExpression1)
        tokenMemberExpression1.addToken(tokenMemberVarSemiColon1)

        tokenMemberVarType2.addToken(tokenMemberVarName2)
        tokenMemberVarName2.addToken(tokenMemberVarSemiColon2)

        // Method's instructions
        val tokenMemberMethodVarType1 = stringAnalyzer3.TokenQualifiedName()
        val tokenMemberMethodVarName1 = stringAnalyzer3.TokenName()
        val tokenMethodSemiColonVar1 = stringAnalyzer3.TokenSemiColon()

        val tokenMemberMethodVarType2 = stringAnalyzer3.TokenQualifiedName()
        val tokenMemberMethodVarName2 = stringAnalyzer3.TokenName()
        val tokenMemberMethodVarEquals2 = stringAnalyzer3.TokenEquals()
        val tokenMemberMethodExpression2 = stringAnalyzer3.TokenExpression1()
        val tokenMethodSemiColonVar2 = stringAnalyzer3.TokenSemiColon()

        val tokenMemberMethodExpression3 = stringAnalyzer3.TokenExpression1()
        val tokenMethodSemiColonVar3 = stringAnalyzer3.TokenSemiColon()

        val tokenMemberMethodVarName4 = stringAnalyzer3.TokenName()
        val tokenMemberMethodVarEquals4 = stringAnalyzer3.TokenEquals()
        val tokenMemberMethodExpression4 = stringAnalyzer3.TokenExpression1()
        val tokenMethodSemiColonVar4 = stringAnalyzer3.TokenSemiColon()

        val tokenType5woEquals = stringAnalyzer3.TokenName()
        val tokenName5woEquals = stringAnalyzer3.TokenName()
        val tokenSemiColon5woEquals = stringAnalyzer3.TokenSemiColon()

        //Variant without end semicolon ";"
        // Method's instructions
        val tokenMemberMethodVarType1wo = stringAnalyzer3.TokenName()
        val tokenMemberMethodVarName1wo = stringAnalyzer3.TokenName()

        val tokenMemberMethodVarType2wo = stringAnalyzer3.TokenName()
        val tokenMemberMethodVarName2wo = stringAnalyzer3.TokenName()
        val tokenMemberMethodVarEquals2wo = stringAnalyzer3.TokenEquals()
        val tokenMemberMethodExpression2wo = stringAnalyzer3.TokenExpression2()

        val tokenMemberMethodExpression3wo = stringAnalyzer3.TokenExpression2()

        val tokenMemberMethodVarName4wo = stringAnalyzer3.TokenName()
        val tokenMemberMethodVarEquals4wo = stringAnalyzer3.TokenEquals()
        val tokenMemberMethodExpression4wo = stringAnalyzer3.TokenExpression2()


        tokenMemberMethodVarType1wo.addToken(tokenMemberMethodVarName1wo)

        tokenMemberMethodVarType2wo.addToken(tokenMemberMethodVarName2wo)
        tokenMemberMethodVarName2wo.addToken(tokenMemberMethodVarEquals2wo)
        tokenMemberMethodVarEquals2wo.addToken(tokenMemberMethodExpression2wo)

        tokenMemberMethodVarName4wo.addToken(tokenMemberMethodVarEquals4wo)
        tokenMemberMethodVarEquals4wo.addToken(tokenMemberMethodExpression4wo)

        class ActionTokenSemiColonInstruction(token: StringAnalyzer3.Token?) : Action3(token) {
            override fun action(): Boolean {
                val instructions = stringAnalyzer3.construct.currentInstructions
                val instruction = Instruction()
                if (tokenMemberMethodVarType1.name != null) {
                    instruction.type = tokenMemberMethodVarType1.name
                }
                if (tokenMemberMethodVarType2.name != null) {
                    instruction.type = tokenMemberMethodVarType2.name
                }
                if (tokenMemberMethodVarName1.name != null) {
                    instruction.name = tokenMemberMethodVarName1.name
                }
                if (tokenMemberMethodVarName2.name != null) {
                    instruction.name = tokenMemberMethodVarName2.name
                    instruction.expression.leftHand = tokenMemberMethodVarName2.name
                }
                if (tokenMemberMethodVarName4.name != null) {
                    instruction.name = tokenMemberMethodVarName4.name
                    instruction.expression.leftHand = tokenMemberMethodVarName2.name
                }
                if (tokenMemberMethodExpression2.expression != null) {
                    instruction.expression.expression = tokenMemberMethodExpression2.expression
                }
                if (tokenMemberMethodExpression4.expression != null) {
                    instruction.expression.expression = tokenMemberMethodExpression4.expression
                }
                instructions.instructionList.add(instruction)
                return true
            }

        }
        ActionTokenSemiColonInstruction(tokenMethodSemiColonVar1)
        ActionTokenSemiColonInstruction(tokenMethodSemiColonVar2)
        ActionTokenSemiColonInstruction(tokenMethodSemiColonVar3)
        ActionTokenSemiColonInstruction(tokenMethodSemiColonVar4)

        tokenMemberMethodVarType2.addToken(tokenMemberMethodVarName2)
        tokenMemberMethodVarName2.addToken(tokenMemberMethodVarEquals2)
        tokenMemberMethodVarEquals2.addToken(tokenMemberMethodExpression2)
        tokenMemberMethodExpression2.addToken(tokenMethodSemiColonVar2)

        tokenMemberMethodVarType1.addToken(tokenMemberMethodVarName1)
        tokenMemberMethodVarName1.addToken(tokenMethodSemiColonVar1)

        tokenMemberMethodExpression3.addToken(tokenMethodSemiColonVar3)

        tokenMemberMethodVarName4.addToken(tokenMemberMethodVarEquals4)
        tokenMemberMethodVarEquals4.addToken(tokenMemberMethodExpression4)
        tokenMemberMethodExpression4.addToken(tokenMethodSemiColonVar4)


        val tokenMemberVar = stringAnalyzer3.SingleTokenExclusiveXor(
            tokenMemberVarType1,
            tokenMemberVarType2,
            //tokenMemberMethodExpression3,
            //tokenMemberMethodVarName4,
            //tokenMethodSemiColonVar5/*, tokenType5woEquals*/
        )

        val tokenMemberMethodType = stringAnalyzer3.TokenQualifiedName()
        val tokenMemberMethodName = stringAnalyzer3.TokenName()

        // Arguments' list
        val tokenOpenParenthesizedMethodParameter = stringAnalyzer3.TokenOpenParenthesized()
        //tokenOpenParenthesizedMethodParameter.fireOnTrue(true)
        /*val tokenComaMethodParameter1 = stringAnalyzer3.TokenComa()
        val tokenQualifiedNameMethodParameter1 = stringAnalyzer3.TokenName()
        val tokenNameMethodParameter1 = stringAnalyzer3.TokenName()

        //val tokenComaMethodParameter2 = stringAnalyzer3.TokenComa()
        val tokenQualifiedNameMethodParameter2 = stringAnalyzer3.TokenName()
        val tokenNameMethodParameter2 = stringAnalyzer3.TokenName()

        val tokenCloseParenthesizedMethodParameter = stringAnalyzer3.TokenCloseParenthesized()


        val multiTokenOptionalMethodParameter2 = stringAnalyzer3.MultiTokenMandatory(
            tokenComaMethodParameter1, tokenQualifiedNameMethodParameter1, tokenNameMethodParameter1
        )
        val multiTokenOptionalMethodParameter1 = stringAnalyzer3.MultiTokenMandatory(
            tokenQualifiedNameMethodParameter2, tokenNameMethodParameter2
        )

        val multiTokenOptionalMethodParameter = stringAnalyzer3.MultiTokenInclusiveXor(
            multiTokenOptionalMethodParameter1, multiTokenOptionalMethodParameter2
        )

        tokenOpenParenthesizedMethodParameter.addToken(multiTokenOptionalMethodParameter)
        multiTokenOptionalMethodParameter.addToken(tokenCloseParenthesizedMethodParameter)
        //multiTokenOptionalMethodParameter2.addToken(tokenCloseParenthesizedMethodParameter)//???
        val tokenOpenBracketMethod = stringAnalyzer3.TokenOpenBracket()
        */

        // Replace : 1 ()  2 (double a) 3 (double a, ...)
        val oneArgumentOption1 = stringAnalyzer3.TokenQualifiedName()
        val oneArgumentOption2 = stringAnalyzer3.TokenName()
        val oneArgumentOption = stringAnalyzer3.SingleTokenMandatory(oneArgumentOption1, oneArgumentOption2)

        val multipleArgumentsOption1 = stringAnalyzer3.TokenQualifiedName()
        val multipleArgumentsOption2 = stringAnalyzer3.TokenName()
        val multipleArgumentsOption3 = stringAnalyzer3.TokenComa()
        val multipleArgumentsOption5 = stringAnalyzer3.MultiTokenMandatory(
            multipleArgumentsOption3,
            multipleArgumentsOption1,
            multipleArgumentsOption2,
        )
        val multipleArgumentsOption = stringAnalyzer3.SingleTokenMandatory(multipleArgumentsOption5)
        multipleArgumentsOption5.addToken(multipleArgumentsOption)
        val optionsParenthesizedToken =
            stringAnalyzer3.SingleTokenMandatory(
                stringAnalyzer3.MultiTokenOptional(
                    oneArgumentOption,
                    stringAnalyzer3.SingleTokenExclusiveXor(
                        multipleArgumentsOption
                    )
                )
            )

        val tokenCloseParenthesizedMethodParameter = stringAnalyzer3.TokenCloseParenthesized()
//        multipleArgumentsOption1.addToken(multipleArgumentsOption2)
//        multipleArgumentsOption2.addToken(multipleArgumentsOption3)
        tokenMemberMethodName.addToken(tokenOpenParenthesizedMethodParameter)
        tokenOpenParenthesizedMethodParameter.addToken(optionsParenthesizedToken)
        optionsParenthesizedToken.addToken(tokenCloseParenthesizedMethodParameter)

        class ActionTokenOpenParenthesizedMethodParameter(token: StringAnalyzer3.Token?) : Action3(token) {

            override fun action(): Boolean {
                //if (token.isSuccessful) {
                if (tokenMemberMethodType.name != null && tokenMemberMethodName.name != null) {
                    stringAnalyzer3.construct.currentMethod.ofClass = Variable()
                    stringAnalyzer3.construct.currentMethod.ofClass.classStr = tokenMemberMethodType.name
                    stringAnalyzer3.construct.currentMethod.name = tokenMemberMethodName.name

                    if (stringAnalyzer3.construct.currentMethod.instructions.instructionList == null) {
                        stringAnalyzer3.construct.currentMethod.instructions = InstructionBlock()
                    }
                    stringAnalyzer3.construct.pushInstructions(stringAnalyzer3.construct.currentMethod.instructions)
                }
                //}
                return true
            }
        }

        ActionTokenOpenParenthesizedMethodParameter(tokenOpenParenthesizedMethodParameter)

        class ActionParamType(token: StringAnalyzer3.Token?) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {
                    val name = (token as StringAnalyzer3.TokenQualifiedName).name
                    if (name != null) {
                        val parameterList = stringAnalyzer3.construct.currentMethod.parameterList
                        parameterList.add(Variable())
                        if (parameterList.size > 0) {
                            parameterList[parameterList.size - 1].classStr = name
                            (token as StringAnalyzer3.TokenQualifiedName).name = null
                        }
                    }
                }
                return true
            }
        }

        class ActionParamName(token: StringAnalyzer3.Token?) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {
                    val name = (token as StringAnalyzer3.TokenName).name
                    if (name != null) {
                        val parameterList = stringAnalyzer3.construct.currentMethod.parameterList
                        if (parameterList.size == 0)
                            parameterList.add(Variable())
                        parameterList[parameterList.size - 1].name = name
                        (token as StringAnalyzer3.TokenName).name = null
                    }
                }
                return true
            }
        }


        ActionParamType(multipleArgumentsOption1)
        ActionParamType(oneArgumentOption1)
        ActionParamName(multipleArgumentsOption2)
        ActionParamName(oneArgumentOption2)

        class ActionVarType(token: StringAnalyzer3.Token?) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {

                    var name: String? = null
                    if (token == tokenMemberVarSemiColon1 && tokenMemberVarType1.name != null) {
                        name = tokenMemberVarType1.name
                        tokenMemberVarType1.name = null
                    } else if (token == tokenMemberVarSemiColon2 && tokenMemberVarType2.name != null) {
                        name = tokenMemberVarType2.name
                        tokenMemberVarType2.name = null
                    }

                    if (name != null) {
                        val parameterList = stringAnalyzer3.construct.currentClass.variableList
                        val variable = Variable()
                        parameterList.add(variable)
                        variable.classStr = name
                    }
                    name = null
                    if (token == tokenMemberVarSemiColon1) {
                        name = tokenMemberVarName1.name
                        tokenMemberVarName1.name = null
                    } else if (token == tokenMemberVarSemiColon2) {
                        name = tokenMemberVarName2.name
                        tokenMemberVarName2.name = null
                    }
                    if (name != null) {
                        val parameterList = stringAnalyzer3.construct.currentClass.variableList
                        val variable = parameterList.get(parameterList.size - 1)// Variable()
                        variable.name = name
                    }
                }
                return true
            }
        }
        ActionVarType(tokenMemberVarSemiColon1)
        ActionVarType(tokenMemberVarSemiColon2)

        tokenMemberMethodType.addToken(tokenMemberMethodName)
        tokenMemberMethodName.addToken(tokenOpenParenthesizedMethodParameter)

        val tokenIf = stringAnalyzer3.TokenString("if")
        val tokenIfWoElse = stringAnalyzer3.TokenString("if")

        val tokenElse = stringAnalyzer3.TokenString("else")

        val tokenWhile = stringAnalyzer3.TokenString("while")
        val tokenDo = stringAnalyzer3.TokenString("do")

        val tokenForVariantColon = stringAnalyzer3.TokenString("for")
        val tokenForVariantSemiColon = stringAnalyzer3.TokenString("for")
        val tokenForEach = stringAnalyzer3.TokenString("forEach")

        // Array type declaration
        val tokenDeclarationVarType2: StringAnalyzer3.Token = stringAnalyzer3.TokenName2()
        val tokenDeclarationVarName2: StringAnalyzer3.Token = stringAnalyzer3.TokenName2()
        val tokenBracketDeclarationVar1: StringAnalyzer3.Token = stringAnalyzer3.TokenString("[")
        val tokenBracketDeclarationVar2: StringAnalyzer3.Token = stringAnalyzer3.TokenString("]")
        val tokenBracketDeclarationVar3: StringAnalyzer3.Token = stringAnalyzer3.TokenString("[")
        val tokenBracketDeclarationVar4: StringAnalyzer3.Token = stringAnalyzer3.TokenString("]")
        val tokenBracketAfterType = stringAnalyzer3.SingleTokenOptional(tokenBracketDeclarationVar1)
        val tokenBracketAfterName = stringAnalyzer3.SingleTokenOptional(tokenBracketDeclarationVar3)
        val tokenEqualsAssignment = stringAnalyzer3.TokenEquals()
        val tokenBracketAfterDeclarationEquals = stringAnalyzer3.SingleTokenOptional(stringAnalyzer3.TokenString("="))
        val tokenBracketAfterDeclarationSemiColon =
            stringAnalyzer3.SingleTokenOptional(stringAnalyzer3.TokenSemiColon())
        val tokenAssignmentOrDeclaration = stringAnalyzer3.SingleTokenExclusiveXor(
            tokenBracketAfterDeclarationEquals, tokenBracketAfterDeclarationSemiColon
        )
        stringAnalyzer3.SingleTokenOptional(stringAnalyzer3.TokenSemiColon())
        val singleTokenOptionalBracketVarDeclType = stringAnalyzer3.SingleTokenOptional(
            stringAnalyzer3.MultiTokenMandatory(
                tokenBracketDeclarationVar1,
                tokenBracketDeclarationVar2
            )
        )
        val singleTokenOptionalBracketVarDeclName = stringAnalyzer3.SingleTokenOptional(
            stringAnalyzer3.MultiTokenMandatory(
                tokenBracketDeclarationVar3,
                tokenBracketDeclarationVar4
            )
        )
        //val tokenGeneralExpression = stringAnalyzer3.TokenExpression1()

        //tokenBracketAfterDeclarationEquals.addToken(tokenGeneralExpression)


        // Call a method in an expression or chaining methods' call
        val tokenMethodCall: StringAnalyzer3.Token? = stringAnalyzer3.TokenMethodCall()

        // Call a constructor in an expression or chaining methods' call
        val tokenConstructorCall: StringAnalyzer3.Token? = stringAnalyzer3.TokenConstructorCall()
        val instruction = stringAnalyzer3.MultiTokenExclusiveXor(
            // Test keywords first.
            stringAnalyzer3.SingleTokenExclusiveXor(tokenIf, tokenIfWoElse),
            /*tokenCallMethod,*/ tokenDo,
            tokenWhile,
            tokenForVariantColon,
            tokenForVariantSemiColon,
            tokenMemberMethodVarType1,
            tokenMemberMethodVarType2,
            tokenMemberMethodExpression3,
            tokenMemberMethodVarName4,
            stringAnalyzer3.TokenSemiColon(),////////////////////////
        )
        val instructionIncr = stringAnalyzer3.SingleTokenExclusiveXor(
            tokenMemberMethodExpression3wo,
            tokenMemberMethodVarType2wo,
            tokenMemberMethodVarType1wo,
            tokenMemberMethodVarName4wo
        )
        // Instructions
        val tokenSingleInstructionIf = stringAnalyzer3.SingleTokenMandatory(instruction)
        val tokenSingleInstructionIfWoElse = stringAnalyzer3.SingleTokenMandatory(instruction)//.copy(instruction)
        val tokenSingleInstructionVariantSemiColon1 =
            stringAnalyzer3.SingleTokenMandatory(instructionIncr)//.copy(instructionIncr)
        val tokenSingleInstructionVariantSemiColon2 =
            stringAnalyzer3.SingleTokenMandatory(instructionIncr)//.copy(instructionIncr)
        val tokenSingleInstructionElse = stringAnalyzer3.SingleTokenMandatory(instruction)//.copy(instruction)
        val tokenSingleInstructionDo = stringAnalyzer3.SingleTokenMandatory(instruction)//.copy(instruction)
        val tokenSingleInstructionWhile = stringAnalyzer3.SingleTokenMandatory(instruction)//.copy(instruction)
        val tokenSingleInstructionForVariantSemiColon =
            stringAnalyzer3.SingleTokenMandatory(instruction)//.copy(instruction)
        val tokenSingleInstructionForVariantColon = stringAnalyzer3.SingleTokenMandatory(instruction)
        val tokenMultiMembersInstructions = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsWhile = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsIf = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsIfWoElse = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsForVariantSemiColon1 = stringAnalyzer3.MultiTokenExclusiveXor(instructionIncr)
        val tokenMultiMembersInstructionsForVariantSemiColon2 = stringAnalyzer3.MultiTokenExclusiveXor(instructionIncr)
        val tokenMultiMembersInstructionsElse = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsDo = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsForVariantSemiColon = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val tokenMultiMembersInstructionsForVariantColon = stringAnalyzer3.MultiTokenExclusiveXor(instruction)
        val instructionBlockIncr = stringAnalyzer3.MultiTokenExclusiveXor(instructionIncr)
        // End of Instructions

        val tokenOpenBracketMethod = stringAnalyzer3.TokenOpenBracket()
        val tokenCloseBracketMethod = stringAnalyzer3.TokenCloseBracket()

        tokenCloseParenthesizedMethodParameter.addToken(tokenOpenBracketMethod)
        tokenOpenBracketMethod.addToken(stringAnalyzer3.SingleTokenOptional(tokenMultiMembersInstructions))
        tokenMultiMembersInstructions.addToken(tokenCloseBracketMethod)

        // Instructions' flow controls (if-else, while, do while, for-i, for-:, switch)

        // Block without controls
        val instructionBlockOpenBracket = stringAnalyzer3.TokenOpenBracket()
        val instructionBlockCloseBracket = stringAnalyzer3.TokenCloseBracket()
        val instructionBlockMethod = stringAnalyzer3.SingleTokenMandatory(
            stringAnalyzer3.TokenOpenBracket(), tokenMultiMembersInstructions, stringAnalyzer3.TokenCloseBracket()
        )
        val instructionBlockWhile = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsWhile, instructionBlockCloseBracket
        )
        val instructionBlockIf = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsIf, instructionBlockCloseBracket
        )

        val instructionBlockIfWoElse = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsIfWoElse, instructionBlockCloseBracket
        )
        val instructionIncrForVariantSemiColon1 = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsForVariantSemiColon1, instructionBlockCloseBracket
        )
        val instructionIncrForVariantSemiColon2 = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsForVariantSemiColon2, instructionBlockCloseBracket
        )
        val instructionBlockElse = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsElse, instructionBlockCloseBracket
        )
        val instructionBlockForVariantColon = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsForVariantColon, instructionBlockCloseBracket
        )
        val instructionBlockForVariantSemiColon = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsForVariantSemiColon, instructionBlockCloseBracket
        )
        val instructionBlockDo = stringAnalyzer3.SingleTokenMandatory(
            instructionBlockOpenBracket, tokenMultiMembersInstructionsDo, instructionBlockCloseBracket
        )
        // Logical expression
        val logicalExpressionExpression = stringAnalyzer3.TokenLogicalExpression1()
        val logicalExpressionIf = stringAnalyzer3.SingleTokenMandatory(logicalExpressionExpression)
        val logicalExpressionIfWoElse = stringAnalyzer3.SingleTokenMandatory(logicalExpressionExpression)


        val logicalExpressionWhile = stringAnalyzer3.SingleTokenMandatory(logicalExpressionExpression)
        val logicalExpressionDo = stringAnalyzer3.SingleTokenMandatory(logicalExpressionExpression)

        // if flow control

        val instructionsIf = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionBlockIf, tokenSingleInstructionIf
        )
        val instructionsIfWoElse = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionBlockIfWoElse, tokenSingleInstructionIfWoElse
        )
        val instructionsWoSemiColonForVariantSemiColon1 = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionIncrForVariantSemiColon1, tokenSingleInstructionVariantSemiColon1
        )
        val instructionsWoSemiColonForVariantSemiColon2 = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionIncrForVariantSemiColon2, tokenSingleInstructionVariantSemiColon2
        )
        val instructionsForVariantColon = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionBlockForVariantColon, tokenSingleInstructionForVariantColon
        )

        val instructionsElse = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionBlockElse, tokenSingleInstructionElse
        )
        val instructionsWhile = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionBlockWhile, tokenSingleInstructionWhile
        )
        val instructionsDo = stringAnalyzer3.SingleTokenExclusiveXor(
            instructionBlockDo, tokenSingleInstructionDo
        )
        val instructionsForVariantSemiColon = stringAnalyzer3.SingleTokenExclusiveXor(
            tokenSingleInstructionForVariantSemiColon, instructionBlockForVariantSemiColon
        )

        tokenIf.addToken(logicalExpressionIf)
        logicalExpressionIf.addToken(instructionsIf)
        instructionsIf.addToken(stringAnalyzer3.SingleTokenOptional(tokenElse))
        tokenElse.addToken(instructionsElse)

        tokenIfWoElse.addToken(logicalExpressionIfWoElse)
        logicalExpressionIfWoElse.addToken(instructionsIfWoElse)


        //tokenCloseParenthesizedMethodParameter.addToken(instructionBlockMethod)

        tokenWhile.addToken(logicalExpressionWhile)
        logicalExpressionWhile.addToken(instructionsWhile)


        //tokenDo.addToken(logicalExpressionDo)
        //logicalExpressionDo.addToken(instructionsWhile)
        val tokenOpenParenthesizedForColon = stringAnalyzer3.TokenOpenParenthesized()
        val tokenTypeForColon = stringAnalyzer3.TokenName()
        val tokenNameForColon = stringAnalyzer3.TokenName()
        val tokenColonForColon = stringAnalyzer3.TokenString(":")
        val tokenExpressionForColon = stringAnalyzer3.TokenExpression1()
        val tokenCloseParenthesizedForColon = stringAnalyzer3.TokenCloseParenthesized()
        tokenForVariantColon.addToken(tokenOpenParenthesizedForColon)
        tokenOpenParenthesizedForColon.addToken(tokenTypeForColon)
        tokenTypeForColon.addToken(tokenNameForColon)
        tokenNameForColon.addToken(tokenColonForColon)
        tokenColonForColon.addToken(tokenExpressionForColon)
        tokenExpressionForColon.addToken(tokenCloseParenthesizedForColon)
        tokenCloseParenthesizedForColon.addToken(instructionsForVariantColon)


        val tokenOpenParenthesizedForSemiColon = stringAnalyzer3.TokenOpenParenthesized()
        val tokenSemiColonFor11SemiColon = stringAnalyzer3.TokenString(";")
        val tokenVarForSemiColonExitCondition = stringAnalyzer3.TokenLogicalExpression1()
        val tokenSemiColonFor12SemiColon = stringAnalyzer3.TokenString(";")
        val tokenCloseParenthesizedForSemiColon = stringAnalyzer3.TokenCloseParenthesized()

        tokenForVariantSemiColon.addToken(tokenOpenParenthesizedForSemiColon)
        tokenOpenParenthesizedForSemiColon.addToken(tokenSingleInstructionVariantSemiColon1)
        tokenSingleInstructionVariantSemiColon1.addToken(tokenSemiColonFor11SemiColon)
        tokenSemiColonFor11SemiColon.addToken(tokenVarForSemiColonExitCondition)
        tokenVarForSemiColonExitCondition.addToken(tokenSemiColonFor12SemiColon)
        tokenSemiColonFor12SemiColon.addToken(tokenSingleInstructionVariantSemiColon2)
        tokenSingleInstructionVariantSemiColon2.addToken(tokenCloseParenthesizedForSemiColon)
        tokenCloseParenthesizedForSemiColon.addToken(instructionsForVariantSemiColon)


        class ActionExpressionType(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {
                    if (stringAnalyzer3.construct.currentInstructions != null) {
                        val instructions = stringAnalyzer3.construct.currentInstructions.instructionList
                        if (token == tokenMethodSemiColonVar1) {
                            if (tokenMemberMethodVarType1.name != null) {
                                val name = tokenMemberMethodVarType1.name
                                //tokenMemberMethodVarType1.name = null
                                instructions.add(Instruction())
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).setType(name)
                                tokenMemberMethodVarType1.name = null
                            }
                            if (tokenMemberMethodVarName1.name != null) {
                                val name = tokenMemberMethodVarName1.name
                                val get = instructions.get(instructions.size - 1) as Instruction
                                get.setName(name)
                                get.expression.leftHand = name
                                tokenMemberMethodVarName1.name = null
                            }
                        }
                        if (token == tokenMethodSemiColonVar2) {
                            if (tokenMemberMethodVarType2.name != null) {
                                val name = tokenMemberMethodVarType2.name
                                instructions.add(Instruction())
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).setType(name)
                                tokenMemberMethodVarType2.name = null
                            }
                            if (tokenMemberMethodVarName2.name != null) {
                                var name = tokenMemberMethodVarName2.name
                                //instructions.add(Instruction())
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).setName(name)
                                get.expression.leftHand = name
                                tokenMemberMethodVarName2.name = null
                            }
                        }
                        if (token == tokenMethodSemiColonVar4) {
                            if (tokenMemberMethodVarName4.name != null) {
                                var name = tokenMemberMethodVarName4.name
                                //tokenMemberMethodVarName2.name = null
                                instructions.add(Instruction())
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).setName(name)
                                get.expression.leftHand = name
                                tokenMemberMethodVarName4.name = null
                            }
                        }
                        if (token == tokenMethodSemiColonVar2) {
                            if (tokenMemberMethodExpression2.toString() != null) {
                                var name = tokenMemberMethodExpression2.toString()
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).getExpression().expression = name
                                //tokenMemberMethodExpression2.expression = null
                            }
                        }
                        if (token == tokenMethodSemiColonVar3) {
                            if (tokenMemberMethodExpression3.toString() != null) {
                                var name = tokenMemberMethodExpression3.toString()
                                instructions.add(Instruction())
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).getExpression().expression = name
                                //tokenMemberMethodExpression3.expression = null
                            }
                        }
                        if (token == tokenMethodSemiColonVar4) {
                            if (tokenMemberMethodExpression4.toString() != null) {
                                val name = tokenMemberMethodExpression4.toString()
                                val get = instructions.get(instructions.size - 1)
                                (get as Instruction).getExpression().expression = name
                                //tokenMemberMethodExpression4.expression = null
                            }
                        }
                    }
                }
                return true
            }
        }

        ActionExpressionType(tokenMethodSemiColonVar1)
        ActionExpressionType(tokenMethodSemiColonVar2)
        ActionExpressionType(tokenMethodSemiColonVar3)
        ActionExpressionType(tokenMethodSemiColonVar4)
        //ActionExpressionType(tokenMethodSemiColonVar5)


        class ActionExpressionTypeWithoutSemiColon(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {
                    val instructions = stringAnalyzer3.construct.currentInstructions.instructionList
                    val get = instructions.first() as Instruction
                    if (token == tokenMemberMethodVarName1wo) {
                        if (tokenMemberMethodVarType1wo.name != null) {
                            val name = tokenMemberMethodVarType1wo.name
                            (get as Instruction).setType(name)
                            tokenMemberMethodVarType1wo.name = null
                        }
                        if (tokenMemberMethodVarName1wo.name != null) {
                            val name = tokenMemberMethodVarName1wo.name
                            get.setName(name)
                            get.expression.leftHand = name
                            tokenMemberMethodVarName1wo.name = null
                        }
                    }
                    if (token == tokenMemberMethodExpression2wo) {
                        if (tokenMemberMethodVarType2wo.name != null) {
                            val name = tokenMemberMethodVarType2wo.name
                            (get as Instruction).setType(name)
                            tokenMemberMethodVarType2wo.name = null
                        }
                        if (tokenMemberMethodVarName2wo.name != null) {
                            val name = tokenMemberMethodVarName2wo.name
                            (get as Instruction).setName(name)
                            get.expression.leftHand = name
                            tokenMemberMethodVarName2wo.name = null
                        }
                    }
                    if (token == tokenMemberMethodExpression4wo) {
                        if (tokenMemberMethodVarName4wo.name != null) {
                            val name = tokenMemberMethodVarName4wo.name
                            (get as Instruction).setName(name)
                            get.expression.leftHand = name
                            tokenMemberMethodVarName4wo.name = null
                        }
                    }
                    if (token == tokenMemberMethodExpression2wo) {
                        if (tokenMemberMethodExpression2wo.toString() != null) {
                            val name = tokenMemberMethodExpression2wo.toString()
                            (get as Instruction).getExpression().expression = name
                            //tokenMemberMethodExpression2wo.expression = null
                        }
                    }
                    if (token == tokenMemberMethodExpression3wo) {
                        if (tokenMemberMethodExpression3wo.toString() != null) {
                            val name = tokenMemberMethodExpression3wo.toString()
                            (get as Instruction).getExpression().expression = name
                            //tokenMemberMethodExpression3wo.expression = null
                        }
                    }
                    if (token == tokenMemberMethodExpression4wo) {
                        if (tokenMemberMethodExpression4wo.toString() != null) {
                            val name = tokenMemberMethodExpression4wo.toString()
                            (get as Instruction).getExpression().expression = name
                            //tokenMemberMethodExpression4wo.expression = null
                        }
                    }
                }
                return true
            }
        }

        ActionExpressionTypeWithoutSemiColon(tokenMemberMethodVarName1wo)
        ActionExpressionTypeWithoutSemiColon(tokenMemberMethodExpression2wo)
        ActionExpressionTypeWithoutSemiColon(tokenMemberMethodExpression3wo)
        ActionExpressionTypeWithoutSemiColon(tokenMemberMethodExpression4wo)

        class ActionPushMethod(token: StringAnalyzer3.Token?) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {
                    if (tokenMemberMethodName.name != null && !tokenMemberMethodName.name.isEmpty() && tokenMemberMethodType.name != null && !tokenMemberMethodType.name.isEmpty()) {
                        val methodList = stringAnalyzer3.construct.currentClass.methodList
                        methodList.add(stringAnalyzer3.construct.currentMethod)
                        stringAnalyzer3.construct.popInstructions()
                        stringAnalyzer3.construct.currentMethod = Method()
                        tokenMemberMethodName.name = null
                        tokenMemberMethodType.name = null
                    } else {

                    }
                }
                return true
            }
        }
        ActionPushMethod(tokenCloseBracketMethod)

        class ActionPopContext(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                val popInstructions = stringAnalyzer3.construct.popInstructions()
                stringAnalyzer3.construct.currentInstructions.instructionList.add(popInstructions)
                return true
            }
        }

        tokenPackage.addToken(tokenPackageName)
        tokenPackageName.addToken(tokenPackageSemicolon)
        val tokenPackageOptional =
            stringAnalyzer3.SingleTokenOptional(
                tokenPackage
            )
        tokenImport.addToken(tokenImportName)
        tokenImportName.addToken(tokenImportSemicolon)

        val multiTokenMandatoryImport =
            stringAnalyzer3.MultiTokenOptional(
                tokenImport
            )

        tokenPackageOptional.addToken(multiTokenMandatoryImport)
        multiTokenMandatoryImport.addToken(tokenClass)
        tokenClass.addToken(tokenClassName)
        tokenClassName.addToken(tokenOpenBracketClass)

        val choiceMethodMemberOrVar: Token = stringAnalyzer3.SingleTokenExclusiveXor(
            //tokenNewTypeConstructorMember, //// Function name==class name, return type empty
            tokenMemberMethodType,
            tokenMemberVar,
            tokenCloseBracketClass
        )

        val multiTokenOptionalMethodMember = stringAnalyzer3.MultiTokenOptional(choiceMethodMemberOrVar)
        tokenOpenBracketClass.addToken(multiTokenOptionalMethodMember)
        multiTokenOptionalMethodMember.addToken(tokenCloseBracketClass)
        class ActionCloseBracketClass(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                if (token.isSuccessful) {
                    if (stringAnalyzer3.construct.classesWithName(stringAnalyzer3.construct.currentClass.name).size == 0) {
                        stringAnalyzer3.construct.classes.add(stringAnalyzer3.construct.currentClass)
                        stringAnalyzer3.construct.currentClass = Class()
                        //stringAnalyzer3.construct.pushInstructions(InstructionBlock())

                    }
                }
                return true
            }
        }
        ActionCloseBracketClass(tokenCloseBracketClass)

        class ActionIf(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                try {
                    cloneTokenVersion()
                    val expression =
                        (logicalExpressionIf.choices[0] as StringAnalyzer3.TokenLogicalExpression1).expression
                    if (expression != null) {
                        val value: ControlledInstructions.If = ControlledInstructions.If(expression)
                        stringAnalyzer3.construct.currentInstructions.instructionList.add(value)
                        value.instructionList.add(InstructionBlock())
                        stringAnalyzer3.construct.pushInstructions(value)
                        tokenIf.isSuccessful = false
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }


        }

        // REFACTOR
        class ActionIfInstructions(token: StringAnalyzer3.Token) : Action3(token) {
            init {
                //on = ON_RETURNS_TRUE_NEXT_TOKEN
            }

            override fun action(): Boolean {
                val instructionList: MutableList<InstructionBlock>
                stringAnalyzer3.construct.popInstructions()
                instructionList = stringAnalyzer3.construct.currentInstructions.instructionList
                if (instructionList.size > 0) {
                    val instructionIf = instructionList.get(instructionList.size - 1)
                    if (instructionIf is ControlledInstructions.If) {
                    }
                }
                return true
            }
        }

        class ActionElse(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                val instructionList: MutableList<InstructionBlock>
                //stringAnalyzer3.construct.popInstructions()
                instructionList = stringAnalyzer3.construct.currentInstructions.instructionList
                if (instructionList.size > 0) {
                    val instructionIf = instructionList.get(instructionList.size - 1)
                    if (instructionIf is ControlledInstructions.If) {
                        stringAnalyzer3.construct.pushInstructions(instructionIf.instructionsElse)
                    }
                }
                token.isSuccessful = false
                //}
                return true
            }
        }

        class ActionElseInstructions(token: StringAnalyzer3.Token) : Action3(token) {
            init {
                on = ON_RETURNS_TRUE_NEXT_TOKEN
            }

            override fun action(): Boolean {
                try {
                    if (token.isSuccessful) {
                        //if (stringAnalyzer3.construct.currentInstructions != null //&& stringAnalyzer3.construct.currentInstructions.instructionList.size > 0
                        //) {
                        stringAnalyzer3.construct.popInstructions()
                        //} else {
                        //    throw EmptyEndOfBlockList("ElseInstruction : empty after end of block instructions\' list");
                        //}
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }
        }




        ActionIf(logicalExpressionIf)
        ActionIfInstructions(instructionsIf)
        ActionElse(tokenElse)
        ActionElseInstructions(instructionsElse)


        class ActionIfWoElse(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                try {
                    revertOneVersionAhead()
                    val expression =
                        (logicalExpressionIf.choices[0] as StringAnalyzer3.TokenLogicalExpression1).expression
                    if (expression != null) {
                        val value: ControlledInstructions.If = ControlledInstructions.If(expression)
                        stringAnalyzer3.construct.currentInstructions.instructionList.add(value)
                        //value.instructionList.add(InstructionBlock())
                        stringAnalyzer3.construct.pushInstructions(value)
                        tokenIfWoElse.isSuccessful = false
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }


        }

        // REFACTOR
        class ActionIfWoElseInstructions(token: StringAnalyzer3.Token) : Action3(token) {
            init {
                on = ON_RETURNS_TRUE_NEXT_TOKEN
            }

            override fun action(): Boolean {
                var instructionList: MutableList<InstructionBlock> =
                    stringAnalyzer3.construct.currentInstructions.instructionList
                //stringAnalyzer3.construct.popInstructions()
                instructionList = stringAnalyzer3.construct.currentInstructions.instructionList
                return true
            }
        }
        ActionIfWoElse(logicalExpressionIfWoElse)
        ActionIfWoElseInstructions(instructionsIfWoElse)

        class ActionWhile(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                try {
                    val expression =
                        (logicalExpressionWhile.choices[0] as StringAnalyzer3.TokenLogicalExpression1).expression
                    if (expression != null) {
                        val value: ControlledInstructions.While = ControlledInstructions.While(expression)
                        stringAnalyzer3.construct.currentInstructions.instructionList.add(value)
                        stringAnalyzer3.construct.pushInstructions(value)
                        logicalExpressionExpression.expression = null
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }

        }

        class ActionWhileStart(token: StringAnalyzer3.Token) : Action3(token) {

            override fun action(): Boolean {
                val currentInstructions = stringAnalyzer3.construct.currentInstructions
                if (currentInstructions is ControlledInstructions.While) {
                    val expression =
                        (logicalExpressionWhile.choices[0] as StringAnalyzer3.TokenLogicalExpression1).expression
                    currentInstructions.controlExpression = expression
                }
                return true
            }
        }

        class ActionWhileEnd(token: StringAnalyzer3.Token) : Action3(token) {
            init {
                on = ON_RETURNS_TRUE_NEXT_TOKEN
            }

            override fun action(): Boolean {
                if (token.isSuccessful) {
                    stringAnalyzer3.construct.popInstructions()
                }
                return true
            }
        }

        ActionWhile(tokenWhile)
        ActionWhileStart(logicalExpressionWhile)
        ActionWhileEnd(instructionsWhile)

        class ActionForVariantColon(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                try {
                    if (token.isSuccessful) {
                        val type = tokenTypeForColon.name
                        val name = tokenNameForColon.name
                        val expression = tokenExpressionForColon.expression
                        val instruction1 = Instruction()
                        instruction1.type = type
                        instruction1.name = name
                        instruction1.expression = ListInstructions.Instruction(0, name, expression, expression)
                        val value: ControlledInstructions.For = ControlledInstructions.For(instruction1, expression)
                        stringAnalyzer3.construct.currentInstructions.instructionList.add(value)
                        stringAnalyzer3.construct.pushInstructions(value)
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }

        }

        class ActionForVariantColonEnd(token: StringAnalyzer3.Token) : Action3(token) {
            init {
                on = ON_RETURNS_TRUE_NEXT_TOKEN
            }

            override fun action(): Boolean {
                try {
                    if (token.isSuccessful) {
                        stringAnalyzer3.construct.popInstructions()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }

        }
        ActionForVariantColon(tokenCloseParenthesizedForColon)
        ActionForVariantColonEnd(instructionsForVariantColon)


        class ActionForVariantSemiColon(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                try {
                    println(token.javaClass.name + " " + token.isSuccessful)
                    if (token.isSuccessful) {
                        val value: ControlledInstructions.For = ControlledInstructions.For(null)
                        stringAnalyzer3.construct.currentInstructions.instructionList.add(value)
                        stringAnalyzer3.construct.pushInstructions(value)
                        (value.getFirstForInstruction() as InstructionBlock).instructionList.add(Instruction())
                        stringAnalyzer3.construct.pushInstructions(value.firstForInstruction)
                        token.isSuccessful = false
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }

        }


        class ActionForVariantSemiColonParseInstruction1(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                println(token.javaClass.name + " " + token.isSuccessful)
                if (token.isSuccessful) {
                    val instructionFirst: Instruction =
                        stringAnalyzer3.construct.getCurrentInstructions() as Instruction
                    stringAnalyzer3.construct.popInstructions()
                    val forInstruction: ControlledInstructions.For =
                        stringAnalyzer3.construct.getCurrentInstructions() as ControlledInstructions.For
                    forInstruction.firstForInstruction = instructionFirst.instructionList.get(0) as Instruction?
                    forInstruction.controlExpression = (token as StringAnalyzer3.TokenLogicalExpression1).expression
                    (forInstruction.loopInstruction as InstructionBlock).instructionList.add(Instruction())
                    stringAnalyzer3.construct.pushInstructions(forInstruction.loopInstruction)
                    token.isSuccessful = false
                }
                return true
            }
        }

        class ActionForVariantSemiColonParseInstruction3(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                println(token.javaClass.name + " " + token.isSuccessful)
                if (token.isSuccessful) {
                    val instructionForLoop = stringAnalyzer3.construct.currentInstructions as Instruction
                    stringAnalyzer3.construct.popInstructions()
                    val forInstruction: ControlledInstructions.For =
                        stringAnalyzer3.construct.currentInstructions as ControlledInstructions.For
                    stringAnalyzer3.construct.pushInstructions(forInstruction)
                    forInstruction.loopInstruction = instructionForLoop.instructionList.get(0) as Instruction?
                    (forInstruction.getFirstForInstruction() as InstructionBlock).instructionList.add(Instruction())
                    token.isSuccessful = false
                }
                return true
            }
        }

        class ActionForVariantSemiColonEnd(token: StringAnalyzer3.Token) : Action3(token) {
            init {
                on = ON_RETURNS_TRUE_NEXT_TOKEN
            }

            override fun action(): Boolean {
                println(token.javaClass.name + " " + token.isSuccessful)
                try {
                    stringAnalyzer3.construct.popInstructions()
                    stringAnalyzer3.construct.popInstructions()
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                token.isSuccessful = false
                //}
                return true
            }

        }


        class ActionPrint(token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                //println(token.javaClass.name + " " + token.isSuccessful)
                return true
            }

        }



        ActionPrint(tokenForVariantSemiColon)
        ActionPrint(tokenOpenParenthesizedForSemiColon)
        ActionPrint(tokenSingleInstructionVariantSemiColon1)
        ActionPrint(tokenSemiColonFor11SemiColon)
        ActionPrint(tokenVarForSemiColonExitCondition)
        ActionPrint(tokenSemiColonFor12SemiColon)
        ActionPrint(tokenSingleInstructionVariantSemiColon2)
        ActionPrint(tokenCloseParenthesizedForSemiColon)
        ActionPrint(instructionsForVariantSemiColon)

        ActionForVariantSemiColon(tokenForVariantSemiColon)
        ActionForVariantSemiColonParseInstruction1(tokenVarForSemiColonExitCondition)
        ActionForVariantSemiColonParseInstruction3(tokenCloseParenthesizedForSemiColon)
        ActionForVariantSemiColonEnd(instructionsForVariantSemiColon)


        class ActionDoWhile_Start
            (token: StringAnalyzer3.Token) : Action3(token) {
            override fun action(): Boolean {
                try {
                    val expression =
                        (logicalExpressionDo.choices[0] as StringAnalyzer3.TokenLogicalExpression1).expression
                    val currentInstructions = stringAnalyzer3.construct.currentInstructions
                    if (currentInstructions is ControlledInstructions.DoWhile) {
                        val value: ControlledInstructions.DoWhile = ControlledInstructions.DoWhile(expression)
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    ex.printStackTrace()
                }
                return true
            }
        }

        return tokenPackageOptional
    }


    @Test
    fun testReadMultiSources() {
        val directory_path = "resources/text_parser/"
        this.readDir(directory_path)
    }


    @Test
    fun testDeclarationSimple() {
        val strings2 = ArrayList<String>()

        strings2.add("Double ar;")
        strings2.add("Double[] b;")


        for (s in strings2) {
            val stringAnalyzerJava2: StringAnalyzerJava2 = StringAnalyzerJava2()
            val tokenType2 = stringAnalyzerJava2.TokenName()
            val tokenName2 = stringAnalyzerJava2.TokenName()
            val tokenSemiColon2 = stringAnalyzerJava2.TokenSemiColon()
            tokenType2.addToken(tokenName2)
            tokenName2.addToken(tokenSemiColon2)
            var input = tokenType2.parse(s, 0)
            println("----------------" + s + "-------------------")
            println("---------------------------------------")
            println(tokenType2.toString())
            println("---------------------------------------")
            println(tokenName2.toString())
            println("---------------------------------------")
        }

    }

    @Test
    fun testExpression2() {

        val strings: ArrayList<String> = ArrayList<String>()
        strings.add("name")
        strings.add("1+1")
        strings.add("func2(i1).func3().a")
        strings.add("func2(i1).func3().ab.cd")
        strings.add("func2(i1).func3().func4()")
        strings.add("func2(i1);\nb = func3().a")
        strings.add("pixels[x][y].red")
        strings.add("double [] a = func2(i1);\nb = func3().a")
        strings.add("double double double double double double double double double double")
        strings.add("double double double double; double double double double; double double;")
        strings.add("public double func1(double a,  double b) { double r = -2*a*b; return r+1; }")


        for (s in strings) {
            val stringAnalyzerJava2: StringAnalyzerJava2 = StringAnalyzerJava2()
            var result = true
            println("----------------" + s + "-------------------")
            println(">--------------------------------------")
            var pos = 0
            print(">>")
            var hasNextToken = true
            while (hasNextToken) {
                var tokenExpression2: StringAnalyzerJava2.TokenExpression2 = stringAnalyzerJava2.TokenExpression2()
                var premier = true
                while (pos < s.length - 1 && result) {
                    if (!premier)
                        print(" ")
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
                    print(tokenString.toString() + " ")

                }
                if (tokenExpression2.hasNextToken()) {
                    hasNextToken = true
                    tokenExpression2 = tokenExpression2.nextToken as TokenExpression2

                } else {
                    break
                }
            }
            println("<<")
            println()
            println(">--------------------------------------")
        }


    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val analyzerTestStringAnalyzer9 = TestStringAnalyzer9()
            analyzerTestStringAnalyzer9.testExpression2()
            analyzerTestStringAnalyzer9.testDeclarationSimple()
            analyzerTestStringAnalyzer9.testReadMultiSources()
        }
    }
}