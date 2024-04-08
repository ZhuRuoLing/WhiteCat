package icu.takeneko.whitecat.mirai.data.whitelist

import kotlinx.serialization.Serializable

@Serializable
data class OMMSWhitelistConfiguration(
    val serverHttpApiUrl: String = "http://localhost:50001",
    val token: String = "",
    val groups: Map<String, WhitelistGroupConfiguration>
) {
}

@Serializable
data class WhitelistGroupConfiguration(val description: String = "", val whitelists:List<String> = listOf())