package hellang.compiler.files

interface HelFile {
    fun current(): Char
    fun moveForward(): HelFile
    fun hasNext(): Boolean
    fun moveBackward(): HelFile
    fun hasPrevious(): Boolean
    fun actualPosition(): PositionInFile
}