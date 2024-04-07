package icu.takeneko.whitecat.mirai.data

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val owner: Long,
    val botId: Long,
    val allowOwnerFullAccess: Boolean,
    val availableGroups: List<Long>,
    val groupSettings: Map<String, GroupSetting>,
    val commandPrefix:String
) {
    val allOperators
        get() = groupSettings.map { it.value.operators }.flatten()

    val operators2GroupMap
        get() = buildMap<String,List<Long>> {
            groupSettings.values.forEach { gs ->
                gs.operators.forEach {l ->
                    this[l.toString()] = groupSettings.filter { (k,v) -> l in v.operators }.map { it.key.toLong() }
                }
            }
        }
}



