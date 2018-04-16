package hellang.compiler.files

internal class FileAsString(private val content: String,
                            private val index: Int) : HelFile {
    constructor(content: String) : this(content, 0)

    private val maxSize = content.length

    override fun current() = content[index]

    override fun moveForward(): HelFile = FileAsString(content, index + 1)

    override fun hasNext() = index + 1 < maxSize

    override fun moveBackward(): HelFile = FileAsString(content, index - 1)

    override fun hasPrevious() = index > 0
}