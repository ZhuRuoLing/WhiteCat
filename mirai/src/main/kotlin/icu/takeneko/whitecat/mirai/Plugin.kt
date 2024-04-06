package icu.takeneko.whitecat.mirai

import icu.takeneko.whitecat.mirai.util.BuildProperties
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

class Plugin:KotlinPlugin(
    JvmPluginDescription(
        id = "icu.takeneko.whitecat",
        name = "Whitecat",
        version = BuildProperties["version"]!!
    )
) {
}