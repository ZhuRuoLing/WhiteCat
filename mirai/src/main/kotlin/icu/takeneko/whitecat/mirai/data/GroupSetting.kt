package icu.takeneko.whitecat.mirai.data

import icu.takeneko.whitecat.mirai.data.request.RequestOps
import icu.takeneko.whitecat.mirai.service.WhitelistServiceProviders
import icu.takeneko.whitecat.mirai.service.whitelist.WhitelistManipulateService
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GroupSetting(
    val operators: List<Long>,
    val allowedRequests: List<RequestOps>,
    val whitelistServiceProvider: WhitelistServiceProviders
){
    @Transient
    lateinit var whitelistService: WhitelistManipulateService

    fun initWhitelistService(groupUin:String){
        whitelistService = whitelistServiceProvider.constructor(groupUin)
        whitelistService.initService()
    }
}