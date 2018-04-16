package hellang.compiler.lexer

import hellang.compiler.exceptions.MissingToken
import hellang.compiler.exceptions.UnexpectedToken
import hellang.compiler.files.HelFile
import org.springframework.stereotype.Service
import java.util.*
import java.util.Arrays.stream
import java.util.function.Function
import java.util.stream.Collectors

@Service
class FileLexer(private val moduleLexer: ModuleLexer,
                private val importLexer: ImportLexer) : Lexer {
    override fun lex(helFile: HelFile): HelFileAst {
        val (moduleName, helFileAfterModule) = moduleLexer.lex(helFile)
        val (import, helFileAfterImport) = importLexer.lex(helFileAfterModule) // TODO read multiple imports


        return HelFileAst(
                moduleName,
                listOf(import),
                listOf()
        )
    }
}

interface DefinitionLexer<T> {
    fun canRead(helFile: HelFile): Boolean

    fun lex(helFile: HelFile): Pair<T, HelFile>
}

@Service
class StructLexer(private val definitionModifierLexer: DefinitionModifierLexer,
                  private val lexHelper: LexHelper,
                  private val definitionTokenLexer: DefinitionTokenLexer,
                  private val typeTokenLexer: TypeTokenLexer) : DefinitionLexer<Structure> {
    private val structToken = "struct"
    private val openProperties = '{'
    private val closeProperties = ']'

    override fun canRead(helFile: HelFile): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lex(helFile: HelFile): Pair<Structure, HelFile> {
        val (modifier, afterModifier) = definitionModifierLexer.lex(helFile)
        val (containsStruct, afterStruct) = lexHelper.halFileContainsToken(afterModifier, structToken)
        if (containsStruct.not()) {
            throw RuntimeException() // TODO better exception
        }
        val (structName, afterStructName) = definitionTokenLexer.lex(afterStruct)
        val startOfPropertiesFile = lexHelper.moveAfter(afterStructName, openProperties)
        val (typeToken, afterTypeToken) = typeTokenLexer.lex(startOfPropertiesFile) // TODO load multiple props
        val endOfPropertiesFile = lexHelper.moveAfter(afterTypeToken, closeProperties)
        return Pair(Structure(structName, modifier, listOf(typeToken)), endOfPropertiesFile.moveForward())
    }
}

@Service
class TypeTokenLexer(private val definitionTokenLexer: DefinitionTokenLexer,
                     private val moduleNameLexer: ModuleNameLexer) {
    private val typeModuleSeparator = '/'
    private val listTokenStart = '['
    private val listTokenStop = ']'

    fun lex(helFile: HelFile): Pair<TypeToken, HelFile> {
        val (moduleName, afterModuleName) = lexModuleName(helFile)
        val (definitionToken, afterToken) = definitionTokenLexer.lex(afterModuleName)
        val (list, afterListFile) = lexList(afterToken)
        return Pair(TypeToken(definitionToken, moduleName, list), afterListFile)
    }

    private fun lexModuleName(helFile: HelFile): Pair<ModuleName?, HelFile> =
            if (helFile.current() == typeModuleSeparator) {
                moduleNameLexer.lex(helFile)
            } else {
                Pair(null, helFile)
            }

    private fun lexList(afterToken: HelFile) =
            if (afterToken.current() == listTokenStart) {
                val closeTokenFile = afterToken.moveForward()
                val closeListToken = closeTokenFile.current()
                if (closeListToken == listTokenStop) {
                    Pair(true, closeTokenFile)
                } else {
                    throw UnexpectedToken(closeListToken, listTokenStop)
                }
            } else {
                Pair(false, afterToken)
            }
}

@Service
class DefinitionModifierLexer(private val simpleTokenLexer: SimpleTokenLexer) {
    private val modifiers = stream(DefinitionModifier.values())
            .map { it.toString() }
            .collect(Collectors.joining(", ", "[", "]"))

    fun lex(helFile: HelFile): Pair<DefinitionModifier, HelFile> {
        val (definitionModifier, afterFile) = simpleTokenLexer.lex(helFile)
        val modifier = stream(DefinitionModifier.values())
                .filter { enum -> enum.name == definitionModifier.name }
                .findAny()
                .orElseThrow({ UnexpectedToken(definitionModifier.name, "one of $modifiers") })
        return Pair(modifier, afterFile)
    }
}

@Service
class ImportLexer(private val lexHelper: LexHelper,
                  private val simpleTokenLexer: SimpleTokenLexer,
                  private val definitionTokenLexer: DefinitionTokenLexer) {
    private val importToken = "import"
    private val moduleNameSeparatorToken = '/'

    fun lex(helFile: HelFile): Pair<Import, HelFile> {
        val (containsImportToken, afterImportFile) = lexHelper.halFileContainsToken(helFile, importToken)
        if (containsImportToken.not()) {
            throw MissingToken(importToken, helFile.actualPosition())
        }
        val (fullyQualifiedImport, definitionToken, afterWholeRead) = readPartsOfImportName(afterImportFile)

        return Pair(Import(definitionToken, fullyQualifiedImport), afterWholeRead)
    }

    private fun readPartsOfImportName(helFile: HelFile, tokens: MutableList<SimpleToken> = LinkedList())
            : Triple<List<SimpleToken>, DefinitionToken, HelFile> {
        val char = helFile.current()
        return if (char == moduleNameSeparatorToken) {
            val tokenStart = helFile.moveForward()
            if (definitionTokenLexer.canReadToken(tokenStart)) {
                val (definitionToken, afterRead) = definitionTokenLexer.lex(tokenStart)
                return Triple(tokens, definitionToken, afterRead)
            }
            val (modulePartName, afterRead) = simpleTokenLexer.lex(helFile.moveForward())
            tokens.add(modulePartName)
            readPartsOfImportName(afterRead, tokens)
        } else {
            throw UnexpectedToken(char, "EOL or `/`")
        }
    }
}

@Service
class ModuleLexer(private val lexHelper: LexHelper,
                  private val moduleNameLexer: ModuleNameLexer) {
    private val moduleToken = "module"

    fun lex(helFile: HelFile): Pair<ModuleName, HelFile> {
        val (containsModuleToken, afterModuleFile) = lexHelper.halFileContainsToken(helFile, moduleToken)
        if (containsModuleToken.not()) {
            throw MissingToken(moduleToken, helFile.actualPosition())
        }
        val (name, resultFile) = moduleNameLexer.lex(afterModuleFile.moveForward())
        return Pair(name, resultFile.moveForward())
    }
}

@Service
class ModuleNameLexer(private val simpleTokenLexer: SimpleTokenLexer) {
    private val moduleNameSeparatorToken = '/'
    fun lex(helFile: HelFile, tokens: MutableList<SimpleToken> = LinkedList()): Pair<ModuleName, HelFile> {
        val char = helFile.current()
        return when (char) {
            moduleNameSeparatorToken -> {
                val (modulePartName, afterRead) = simpleTokenLexer.lex(helFile)
                tokens.add(modulePartName)
                lex(afterRead, tokens)
            }
            '\n' -> Pair(ModuleName(tokens), helFile.moveForward())
            else -> throw UnexpectedToken(char, "EOL or `/`")
        }
    }
}

abstract class AbstractTokenLexer<out T : Token>(private val firstLetterRegex: Regex,
                                                 private val letterRegex: Regex,
                                                 private val constructor: Function<String, T>) {
    fun lex(helFile: HelFile): Pair<T, HelFile> {
        val firstLetter = helFile.current()
        if (canReadToken(helFile).not()) {
            throw UnexpectedToken(firstLetter, firstLetterRegex.toString())
        }
        val (token, afterAllFile) = lexRestToken(firstLetter.toString(), helFile.moveForward())
        return Pair(constructor.apply(token), afterAllFile)
    }

    private fun lexRestToken(token: String, helFile: HelFile): Pair<String, HelFile> {
        val letter = helFile.current()
        if (letter.toString().matches(letterRegex).not()) {
            return Pair(token, helFile)
        }
        return lexRestToken(token + letter, helFile.moveForward())
    }

    fun canReadToken(helFile: HelFile) = helFile.current().toString().matches(firstLetterRegex).not()
}

@Service
class SimpleTokenLexer : AbstractTokenLexer<SimpleToken>("[a-z]".toRegex(), "[a-zA-Z0-9-_]*".toRegex(), Function { s: String -> SimpleToken(s) })

@Service
class DefinitionTokenLexer : AbstractTokenLexer<DefinitionToken>("[A-Z]".toRegex(), "[a-zA-Z0-9-_]*".toRegex(), Function { s: String -> DefinitionToken(s) })
