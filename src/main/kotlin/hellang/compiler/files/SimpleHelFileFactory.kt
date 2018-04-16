package hellang.compiler.files

import org.springframework.stereotype.Service
import java.io.File

@Service
internal class SimpleHelFileFactory : HelFileFactory {
    override fun loadFIle(abstractPath: String): HelFile {
        val inputString = File(abstractPath)
                .inputStream()
                .bufferedReader()
                .use { it.readText() }
        return FileAsString(inputString)
    }
}