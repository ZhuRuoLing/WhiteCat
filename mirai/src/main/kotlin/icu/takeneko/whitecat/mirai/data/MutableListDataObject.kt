package icu.takeneko.whitecat.mirai.data

import icu.takeneko.whitecat.mirai.util.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import java.nio.file.Path
import kotlin.io.path.*

class MutableListDataObject<E>(
    parentPath: Path,
    name: String,
    private val default: List<E>,
    private val elemSerializer: KSerializer<E>
) {
    private val serializer = ListSerializer(elemSerializer)
    private lateinit var instance: MutableList<E>
    private val filePath = (parentPath / "$name.json")

    fun load() {
        if (!filePath.exists()) {
            instance = default.toMutableList()
            save()
        }
        filePath.reader(Charsets.UTF_8).use {
            instance = json.decodeFromString(serializer, it.readText()).toMutableList()
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

    fun <R> modify(fn: MutableList<E>.() -> R): R {
        return instance.run(fn).also { save() }
    }

    fun <R> run(fn: MutableList<E>.() -> R): R {
        return instance.run(fn)
    }
}