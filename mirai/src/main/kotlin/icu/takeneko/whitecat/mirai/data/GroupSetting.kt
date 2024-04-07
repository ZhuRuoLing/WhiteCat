package icu.takeneko.whitecat.mirai.data

import icu.takeneko.whitecat.mirai.service.WhitelistServiceProviders
import icu.takeneko.whitecat.mirai.service.whitelist.WhitelistManipulateService
import kotlinx.serialization.Serializable

@Serializable
data class GroupSetting(
    val operators: List<Long>,
    val allowedRequests: List<RequestOps>,
    val whitelistServiceProvider: WhitelistServiceProviders
){
    lateinit var whitelistService: WhitelistManipulateService

    fun initWhitelistService(groupUin:Long){
        whitelistService = whitelistServiceProvider.constructor(groupUin)
        whitelistService.initService()
    }
}