package icu.takeneko.whitecat.mirai.command

import icu.takeneko.whitecat.mirai.data.Data.config


val helpCommand = LiteralCommand("help") {
    requires {
        this.groupUin in config.get().availableGroups || this.senderUin in config.get().allOperators
    }
    execute {
        sendFeedback("=> Help\n" + buildString {
            if (this@execute.source.isFromGroup) {
                append("${config.get().commandPrefix}help -> Show the help message\n")
                append("${config.get().commandPrefix}wl tgt -> List all server targets\n")
                append("${config.get().commandPrefix}wl req a <PlayerName> -> Request to add whitelist\n")
                append("${config.get().commandPrefix}wl req r <PlayerName> -> Request to remove whitelist\n")
                append("${config.get().commandPrefix}wl req a <PlayerName> into <ServerTarget> -> Request to add whitelist into exactly server target\n")
                append("${config.get().commandPrefix}wl req r <PlayerName> into <ServerTarget> -> Request to remove whitelist into exactly server target\n")
            } else {
                append("${config.get().commandPrefix}help -> Show the help message\n")
                append("${config.get().commandPrefix}wl tgt -> List all server targets\n")
                append("${config.get().commandPrefix}wl req p -> List all whitelist requests\n")
                append("${config.get().commandPrefix}wl approve <PlayerName> -> Approve whitelist requests from <PlayerName>\n")
                append("${config.get().commandPrefix}wl approve <PlayerName> <RequestId> -> Approve whitelist requests from <PlayerName> with specified <RequestId>\n")
                append("${config.get().commandPrefix}wl approve <PlayerName> into <ServerTarget> -> Approve a whitelist request to apply exactly into server target\n")
                append("${config.get().commandPrefix}wl approve <PlayerName> <RequestId> into <ServerTarget> -> Approve a whitelist request to apply exactly into server target\n")
                append("${config.get().commandPrefix}wl refuse <PlayerName> -> Refuse all whitelist requests from <PlayerName>\n")
                append("${config.get().commandPrefix}wl refuse <PlayerName> <RequestId> -> Refuse whitelist requests from <PlayerName> with specified <RequestId>\n")
            }
        })
        0
    }
}