package icu.takeneko.whitecat.mirai.data.http

import kotlinx.serialization.Serializable

@Serializable
data class WhitelistQueryData(val whitelistName: String, val players: List<String>)