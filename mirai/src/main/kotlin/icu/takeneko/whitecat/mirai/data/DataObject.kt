package icu.takeneko.whitecat.mirai.data

import icu.takeneko.whitecat.mirai.util.json
import kotlinx.serialization.KSerializer
import java.nio.file.Path
import kotlin.io.path.*

class DataObject<O : Any>(
    parentPath: Path,
    name: String,
    private val default: O,
    private val serializer: KSerializer<O>
) {

    private lateinit var instance: O
    private val filePath = (parentPath / "$name.json")

    fun load() {
        if (!filePath.exists()) {
            instance = default
            save()
        }
        filePath.reader(Charsets.UTF_8).use {
            instance = json.decodeFromString(serializer, it.readText())
        }
    }

    fun save() {
        filePath.deleteIfExists()
        filePath.createFile()
        filePath.writer(Charsets.UTF_8).use {
            it.write(json.encodeToString(serializer, instance))
        }
    }

    fun get() = instance

    fun <R> modify(fn: O.() -> R): R {
        return instance.run(fn).also { save() }
    }

    fun <R> run(fn: O.() -> R): R {
        return instance.run(fn)
    }
}