package hellang.compiler

import hellang.compiler.files.HelFileFactory
import hellang.compiler.profiles.NotTestProfile
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.*

@SpringBootApplication
class HelLangApplication

fun main(args: Array<String>) {
    runApplication<HelLangApplication>(*args)
}

@NotTestProfile
@Component
internal class CompilerRunner(private val helFileFactory: HelFileFactory) : CommandLineRunner {
    override fun run(vararg args: String?) {
        Arrays.stream(args)
                .filter { filePath -> filePath != null }
                .map { filePath -> filePath!! }
                .map { filePath -> helFileFactory.loadFIle(filePath) }
    }
}