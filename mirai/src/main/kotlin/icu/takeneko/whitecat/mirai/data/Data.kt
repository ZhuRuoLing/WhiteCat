package icu.takeneko.whitecat.mirai.data

import kotlinx.serialization.builtins.ListSerializer
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists



object Data {
    private val dataPath = Path("./data")
    val config = DataObject(
        dataPath,
        "config",
        Config(
            0,
            0,
            false,
            listOf(),
            mapOf(),
            "!"
        ), Config.serializer()
    )
    val whitelistRequests = DataObject(
        dataPath,
        "whitelistRequests",
        listOf(),
        ListSerializer(WhitelistRequest.serializer())
    )

    fun loadAll() {
        config.load()
        whitelistRequests.load()
    }

    fun saveAll() {
        config.save()
        whitelistRequests.load()
    }

    init {
        if (!dataPath.exists()) {
            dataPath.createDirectory()
        }
    }


}


