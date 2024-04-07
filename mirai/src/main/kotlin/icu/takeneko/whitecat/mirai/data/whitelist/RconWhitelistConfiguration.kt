package icu.takeneko.whitecat.mirai.data.whitelist

import kotlinx.serialization.Serializable

@Serializable
data class RconWhitelistConfiguration(val servers: Map<String, RconServerConfiguration>, val targets: Map<String, TargetConfiguration>) {
}

@Serializable
data class RconServerConfiguration(
    val address: String,
    val port: Int,
    val password:String,
    val addWhitelistCommand:String = "whitelist add %playerName%",
    val removeWhitelistCommand:String = "whitelist remove %playerName%",
    val addWhitelistResultPattern:String = "Added [a-zA-Z][a-zA-Z0-9_]+ to the whitelist",
    val removeWhitelistResultPattern:String = "Removed [a-zA-Z][a-zA-Z0-9_]+ from the whitelist"
)

@Serializable
data class TargetConfiguration(val servers:List<String>, val description: String)