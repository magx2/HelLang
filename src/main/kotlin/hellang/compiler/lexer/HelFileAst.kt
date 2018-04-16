package hellang.compiler.lexer

class HelFileAst(val moduleName: ModuleName,
                 val imports: List<Import>,
                 val definitions: List<Definition>)

interface Token

data class SimpleToken(val name: String) : Token {
    constructor(char: Char) : this(char.toString())

    operator fun plus(char: Char) = SimpleToken(name + char.toString())
}

data class DefinitionToken(val name: String) : Token {
    constructor(char: Char) : this(char.toString())

    operator fun plus(char: Char) = DefinitionToken(name + char.toString())
}

data class ModuleName(val name: List<SimpleToken>) {
    init {
        if (name.isEmpty()) {
            throw IllegalStateException("`name` cannot be empty!")
        }
    }
}

data class Import(val definitionToken: DefinitionToken,
                  val fullyQualifiedImport: List<SimpleToken>)

enum class DefinitionModifier {
    PRIVATE, MODULE, EXPORT
}

sealed class Definition

data class Structure(val name: DefinitionToken,
                     val modifier: DefinitionModifier,
                     val properties: List<TypeToken>) : Definition() {
    init {
        if (properties.isEmpty()) {
            throw IllegalStateException("Struct `$name` needs to have at least one property!")
        }
    }
}

data class TypeToken(val name: DefinitionToken, val module: ModuleName?, val list: Boolean = false)

data class StructProperty(val name: SimpleToken, val type: TypeToken)

data class Class(val name: DefinitionToken,
                 val modifier: DefinitionModifier,
                 val fields: List<ClassField>,
                 val methods: List<Method>) : Definition()

data class ClassField(val name: String,
                      val type: Definition)

data class Method(val name: String,
                  val outType: Definition,
                  val inTypes: List<Definition>)

data class Inteface(val name: DefinitionToken,
                    val modifier: DefinitionModifier,
                    val methods: List<Method>) : Definition()