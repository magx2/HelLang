package hellang.compiler.lexer

import hellang.compiler.files.HelFile
import org.springframework.stereotype.Service

interface LexHelper {
    fun halFileContainsToken(helFile: HelFile, token: String): Pair<Boolean, HelFile>
    fun moveAfter(helFile: HelFile, char: Char): HelFile
}

@Service
private class LexHelperImpl : LexHelper {
    override fun halFileContainsToken(helFile: HelFile, token: String): Pair<Boolean, HelFile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveAfter(helFile: HelFile, char: Char): HelFile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

