package icu.takeneko.whitecat.mirai.data

import kotlinx.serialization.Serializable

@Serializable
data class WhitelistRequest(
    val player: String,
    val operation: RequestOps,
    val source: String,
    val sourceDescriptor: String,
    val group: String,
    val groupDescriptor: String,
    val targetDescriptor: String?
) {

}

