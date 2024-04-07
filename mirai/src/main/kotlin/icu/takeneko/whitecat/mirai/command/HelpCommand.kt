package icu.takeneko.whitecat.mirai.command

import icu.takeneko.whitecat.mirai.data.Data.config


val helpCommand = LiteralCommand("help").requires {
    this.groupUin in config.get().availableGroups
}.execute {
    sendFeedback("=> Help\n" + buildString {
        if (this@execute.source.isFromGroup) {
            append("${config.get().commandPrefix}help -> Show the help message")
            append("${config.get().commandPrefix}wl tgt -> List all server targets")
            append("${config.get().commandPrefix}wl req a <PlayerName> -> Request to add whitelist")
            append("${config.get().commandPrefix}wl req r <PlayerName> -> Request to remove whitelist")
            append("${config.get().commandPrefix}wl req a <PlayerName> into <ServerTarget> -> Request to add whitelist into exactly server target")
            append("${config.get().commandPrefix}wl req r <PlayerName> into <ServerTarget> -> Request to remove whitelist into exactly server target")
        } else {
            append("${config.get().commandPrefix}wl req cl -> Clear all whitelist requests")
            append("${config.get().commandPrefix}wl tgt -> List all server targets")
            append("${config.get().commandPrefix}wl req p -> List all whitelist requests")
            append("${config.get().commandPrefix}wl approve <PlayerName> -> Approve a whitelist request")
            append("${config.get().commandPrefix}wl approve <PlayerName> into <ServerTarget> -> Approve a whitelist request to apply exactly into server target")

        }
    })
    0
}