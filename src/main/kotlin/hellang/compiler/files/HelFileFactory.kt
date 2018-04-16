package hellang.compiler.files

interface HelFileFactory {
    fun loadFIle(abstractPath: String): HelFile
}