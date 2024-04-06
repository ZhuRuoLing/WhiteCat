package icu.takeneko.whitecat.mirai.data

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val owner: Long,
    val botId: Long,
    val allowOwnerFullAccess: Boolean,
    val availableGroups: List<Long>,
    val groupSettings: Map<Long, GroupSetting>,
    val commandPrefix:String
) {
    val allOperators
        get() = groupSettings.map { it.value.operators }.flatten()
}



