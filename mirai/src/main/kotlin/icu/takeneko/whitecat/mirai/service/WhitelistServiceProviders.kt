package icu.takeneko.whitecat.mirai.service

import icu.takeneko.whitecat.mirai.service.whitelist.WhitelistManipulateService
import icu.takeneko.whitecat.mirai.service.whitelist.impl.OMMSWhitelistManipulateService
import icu.takeneko.whitecat.mirai.service.whitelist.impl.RconWhitelistManipulateService

enum class WhitelistServiceProviders(val constructor: (String) -> WhitelistManipulateService) {
    RCON({RconWhitelistManipulateService(it)}), OMMS({OMMSWhitelistManipulateService(it)})
}