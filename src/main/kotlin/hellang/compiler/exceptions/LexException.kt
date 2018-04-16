@file:Suppress("unused", "MemberVisibilityCanBePrivate", "CanBeParameter")

package hellang.compiler.exceptions

import hellang.compiler.files.PositionInFile

abstract class LexException(message: String,
                            cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
}

class MissingToken(val missingToken: String, val positionInFile: PositionInFile) :
        LexException("Token `$missingToken` is missing! Cannot parse ${positionInFile.line}:${positionInFile.char}")

class UnexpectedToken(val token: String,
                      val expectedToken: String) : LexException("Got token `$token` but expected $expectedToken!") {
    constructor(token: Char, expectedToken: String) : this(token.toString(), expectedToken)
    constructor(token: Char, expectedToken: Char) : this(token.toString(), expectedToken.toString())
}