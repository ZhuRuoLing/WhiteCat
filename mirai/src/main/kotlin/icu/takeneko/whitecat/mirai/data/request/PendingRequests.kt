package icu.takeneko.whitecat.mirai.data.request

import icu.takeneko.whitecat.mirai.data.Data

object PendingRequests {
    val whitelistRequests = RequestManager(
        { Data.whitelistRequests.get() },
        { Data.whitelistRequests.modify(it) }
    ) { s, t -> t.player == s }
}