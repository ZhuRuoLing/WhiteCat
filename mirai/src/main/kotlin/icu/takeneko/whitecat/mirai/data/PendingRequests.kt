package icu.takeneko.whitecat.mirai.data

object PendingRequests {
    val whitelistRequests = RequestManager(
        { Data.whitelistRequests.get() },
        { Data.whitelistRequests.modify(it) }
    ) { s, t -> t.player == s }
}