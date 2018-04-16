package hellang.compiler.lexer

import hellang.compiler.files.HelFile

interface Lexer {
    fun lex(helFile: HelFile): HelFileAst
}