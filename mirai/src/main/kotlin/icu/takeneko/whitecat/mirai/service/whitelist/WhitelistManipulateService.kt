package icu.takeneko.whitecat.mirai.service.whitelist

interface WhitelistManipulateService {
    fun initService()

    fun add(target: String, player: String)
    fun remove(target: String, player: String)
    fun rename(target: String, oldPlayer: String, newPlayer: String)

    fun listAvailable(): Map<String, String>
}