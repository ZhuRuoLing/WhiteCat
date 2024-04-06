package icu.takeneko.whitecat.mirai.data

import icu.takeneko.whitecat.mirai.service.WhitelistServiceProviders
import kotlinx.serialization.Serializable

@Serializable
data class GroupSetting(
    val operators: List<Long>,
    val allowedRequests: List<RequestOps>,
    val whitelistServiceProvider: WhitelistServiceProviders
)