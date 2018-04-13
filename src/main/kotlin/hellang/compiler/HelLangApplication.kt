package hellang.compiler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelLangApplication

fun main(args: Array<String>) {
    runApplication<HelLangApplication>(*args)
}
