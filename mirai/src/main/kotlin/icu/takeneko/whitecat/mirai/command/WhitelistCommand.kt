package icu.takeneko.whitecat.mirai.command

import icu.takeneko.whitecat.mirai.Plugin
import icu.takeneko.whitecat.mirai.data.*
import icu.takeneko.whitecat.mirai.data.Data.config
import icu.takeneko.whitecat.mirai.data.request.PendingRequests
import icu.takeneko.whitecat.mirai.data.request.RequestOps
import icu.takeneko.whitecat.mirai.data.request.WhitelistRequest
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.asFriendOrNull
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.contact.nameCardOrNick

val whitelistCommand = LiteralCommand("wl") {
    literal("req") {
        literal("a") {
            requires { this.isFromGroup && this.groupUin in config.get().availableGroups && this.groupUin.toString() in config.get().groupSettings.keys }
            wordArgument("playerName") {
                execute {
                    val playerName = getStringArgument("playerName")
                    PendingRequests.whitelistRequests.add(
                        WhitelistRequest(
                            playerName,
                            RequestOps.ADD,
                            this.source.senderUin.toString(),
                            this.source.sender.nameCardOrNick,
                            this.source.groupUin.toString(),
                            this.source.group?.name ?: "unknown",
                            null
                        )
                    )
                    sendFeedback("Requested to add whitelist for $playerName")
                    config.get().getOperators(this.source.groupUin)?.forEach {
                        val member = this.source.group?.getMember(it) ?: return@forEach
                        Plugin.sendAllPendingRequests(member.asFriendOrNull() ?: return@forEach)
                    }
                    0
                }
                literal("into") {
                    wordArgument("serverTarget") {
                        execute {
                            val playerName = getStringArgument("playerName")
                            val serverTarget = getStringArgument("serverTarget")
                            if (serverTarget !in config.get().groupSettings[this.source.groupUin.toString()]!!.whitelistService.listAvailable()) {
                                sendFeedback("$serverTarget not found in all ServerTargets")
                                return@execute 1
                            }
                            PendingRequests.whitelistRequests.add(
                                WhitelistRequest(
                                    playerName,
                                    RequestOps.ADD,
                                    this.source.senderUin.toString(),
                                    this.source.sender.nameCardOrNick,
                                    this.source.groupUin.toString(),
                                    this.source.group?.name ?: "unknown",
                                    serverTarget
                                )
                            )
                            sendFeedback("Requested to add whitelist for $playerName into $serverTarget")
                            config.get().getOperators(this.source.groupUin)?.forEach {
                                val member = this.source.group?.getMember(it) ?: return@forEach
                                Plugin.sendAllPendingRequests(member.asFriendOrNull() ?: return@forEach)
                            }
                            0
                        }
                    }
                }
            }
        }
        literal("r") {
            requires { this.groupUin in config.get().availableGroups && this.groupUin.toString() in config.get().groupSettings.keys }
            wordArgument("playerName") {
                execute {
                    val playerName = getStringArgument("playerName")
                    PendingRequests.whitelistRequests.add(
                        WhitelistRequest(
                            playerName,
                            RequestOps.REMOVE,
                            this.source.senderUin.toString(),
                            this.source.sender.nameCardOrNick,
                            this.source.groupUin.toString(),
                            this.source.group?.name ?: "unknown",
                            null
                        )
                    )
                    sendFeedback("Requested to remove whitelist for $playerName")
                    config.get().getOperators(this.source.groupUin)?.forEach {
                        val member = this.source.group?.getMember(it) ?: return@forEach
                        Plugin.sendAllPendingRequests(member.asFriendOrNull() ?: return@forEach)
                    }
                    0
                }
                literal("into") {
                    wordArgument("serverTarget") {
                        execute {
                            val playerName = getStringArgument("playerName")
                            val serverTarget = getStringArgument("serverTarget")
                            if (serverTarget !in config.get().groupSettings[this.source.groupUin.toString()]!!.whitelistService.listAvailable()) {
                                sendFeedback("$serverTarget not found in all ServerTargets")
                                return@execute 1
                            }
                            PendingRequests.whitelistRequests.add(
                                WhitelistRequest(
                                    playerName,
                                    RequestOps.REMOVE,
                                    this.source.senderUin.toString(),
                                    this.source.sender.nameCardOrNick,
                                    this.source.groupUin.toString(),
                                    this.source.group?.name ?: "unknown",
                                    serverTarget
                                )
                            )
                            sendFeedback("Requested to remove whitelist for $playerName from $serverTarget")
                            config.get().getOperators(this.source.groupUin)?.forEach {
                                val member = this.source.group?.getMember(it) ?: return@forEach
                                Plugin.sendAllPendingRequests(member.asFriendOrNull() ?: return@forEach)
                            }
                            0
                        }
                    }
                }
            }
        }

        literal("p") {
            requires { this.senderUin in config.get().allOperators }
            execute {
                Plugin.sendAllPendingRequests(this.source.sender as Friend)
                0
            }
        }
        literal("cl") {
            requires { this.senderUin in config.get().allOperators }
            execute {
//                val avail = config.get().groupSettings[this.source.groupUin]!!.whitelistService.listAvailable()
//                sendFeedback("=> Whitelist Request Clear")
                0
            }
        }
    }
    literal("tgt") {
        requires { (this.groupUin in config.get().availableGroups && this.groupUin.toString() in config.get().groupSettings.keys) || this.senderUin in config.get().allOperators }
        execute {
            val avail =
                if (this.source.isFromGroup) listOf(config.get().groupSettings[this.source.groupUin.toString()]!!.whitelistService.listAvailable())
                else config.get().operators2GroupMap[this.source.senderUin.toString()]!!.mapNotNull { config.get().groupSettings[it.toString()]?.whitelistService?.listAvailable() }
            sendFeedback("=> All Available ServerTargets\n${
                buildString {
                    avail.forEach {
                        for ((k, v) in it) {
                            append("$k: $v\n")
                        }
                    }
                }
            }")
            0
        }
    }
    literal("approve") {
        requires { this.senderUin in config.get().allOperators }
        wordArgument("playerName") {
            execute {
                val playerName = getStringArgument("playerName")
                approveRequests(this.source) {
                    it.player == playerName
                }
                0
            }
            integerArgument("requestId") {
                execute {
                    val playerName = getStringArgument("playerName")
                    val requestId = getIntegerArgument("requestId")
                    approveRequests(this.source) {
                        it.player == playerName && it.requestId == requestId
                    }
                    0
                }
                literal("into") {
                    wordArgument("serverTarget") {
                        execute {
                            val playerName = getStringArgument("playerName")
                            val requestId = getIntegerArgument("requestId")
                            val serverTarget = getStringArgument("serverTarget")
                            approveRequests(this.source, {
                                WhitelistRequest(
                                    it.player,
                                    it.operation,
                                    it.source,
                                    it.sourceDescriptor,
                                    it.group,
                                    it.groupDescriptor,
                                    serverTarget,
                                    it.requestId
                                )
                            }) {
                                it.player == playerName && it.requestId == requestId
                            }
                            0
                        }
                    }
                }
            }
            literal("into") {
                wordArgument("serverTarget") {
                    execute {
                        val playerName = getStringArgument("playerName")
                        val serverTarget = getStringArgument("serverTarget")
                        approveRequests(this.source, {
                            WhitelistRequest(
                                it.player,
                                it.operation,
                                it.source,
                                it.sourceDescriptor,
                                it.group,
                                it.groupDescriptor,
                                serverTarget,
                                it.requestId
                            )
                        }) {
                            it.player == playerName
                        }
                        0
                    }
                }
            }
        }
    }
    literal("refuse") {
        wordArgument("playerName") {
            execute {
                refuseRequests(this.source) {
                    it.player == getStringArgument("playerName")
                }
                0
            }
            integerArgument("requestId") {
                execute {
                    refuseRequests(this.source) {
                        it.player == getStringArgument("playerName") && it.requestId == getIntegerArgument("requestId")
                    }
                    0
                }
            }
        }
    }
}

fun refuseRequests(commandContext: Context, filter: (WhitelistRequest) -> Boolean) {
    val requests = PendingRequests.whitelistRequests.get().filter(filter)
    if (requests.isEmpty()) {
        commandContext.sendFeedback("No matches for the given filter.")
        return
    }
    val message = buildString {
        append("=> Refusing ${requests.size} request${if (requests.size == 1) "" else "s"}\n")
        requests.forEach {
            append("[${it.requestId}] ${it.sourceDescriptor}(${it.source}) requested to ${it.operation.describe()} ${it.player}${if (it.targetDescriptor == null) "" else " into ${it.targetDescriptor}"}\n")
        }
    }
    PendingRequests.whitelistRequests.removeAll(requests)
    commandContext.sendFeedback(message)
}

fun approveRequests(
    commandContext: Context,
    targetTransformer: (WhitelistRequest) -> WhitelistRequest = { it },
    filter: (WhitelistRequest) -> Boolean
) {
    val requests = PendingRequests.whitelistRequests.get().filter(filter).map(targetTransformer)
    if (requests.isEmpty()) {
        commandContext.sendFeedback("No matches for the given filter.")
        return
    }
    val request2GroupSettingMap = mutableMapOf<WhitelistRequest, GroupSetting>()
    val settings = config.get().groupSettings.map { it.key.toString() to it.value }.toMap()
    val allAvailableServers = settings.values.map { it.whitelistService.listAvailable() }.flatMap { it.keys }
    val message = buildString {
        val allReasons = mutableListOf<String>()
        val reasons = mutableListOf<String>()
        append("=> Approving ${requests.size} request${if (requests.size == 1) "" else "s"}\n")
        requests.forEach {
            if (it.group !in settings) {
                reasons += "Request ${it.requestId} does not come from any existing group"
            }
            if (it.targetDescriptor == null) {
                reasons += "Request ${it.requestId} has not specified any ServerTarget"
            }
            if (it.targetDescriptor !in allAvailableServers) {
                reasons += "Request ${it.requestId} requested ServerTarget[${it.targetDescriptor}] has no matching in any GroupSetting"
            }
            if (reasons.isNotEmpty()) {
                append("[×] [${it.requestId}] ${it.sourceDescriptor}(${it.source}) requested to ${it.operation.describe()} ${it.player}${if (it.targetDescriptor == null) "" else " into ${it.targetDescriptor}"}\n")
                allReasons += reasons
            } else {
                append("[√] [${it.requestId}] ${it.sourceDescriptor}(${it.source}) requested to ${it.operation.describe()} ${it.player}${if (it.targetDescriptor == null) "" else " into ${it.targetDescriptor}"}\n")
                request2GroupSettingMap[it] = settings[it.group]!!
            }
        }
        if (allReasons.isNotEmpty()) {
            append("Requests tagged with [×] will remain unchanged.\n")
            append("=> Reasons:\n")
            allReasons.forEach { append("$it\n") }
        }
    }
    commandContext.sendFeedback(message)
    if (request2GroupSettingMap.isEmpty()) {
        commandContext.sendFeedback("No requests to apply.")
        return
    }
    commandContext.sendFeedback("All checks are finished, applying ${request2GroupSettingMap.size} request${if (request2GroupSettingMap.size == 1) "" else "s"}.")
    val feedbacks = mutableListOf<String>()
    val removes = mutableListOf<WhitelistRequest>()
    request2GroupSettingMap.forEach { (k, v) ->
        try {
            when (k.operation) {
                RequestOps.ADD -> {
                    feedbacks.add("Adding ${k.player} to ${k.targetDescriptor}")
                    v.whitelistService.add(k.targetDescriptor!!, k.player)
                }

                RequestOps.REMOVE -> {
                    feedbacks.add("Removing ${k.player} from ${k.targetDescriptor}")
                    v.whitelistService.remove(k.targetDescriptor!!, k.player)
                }
            }
            removes += k
        } catch (t: Throwable) {
            val exMessage = if (t is RuntimeException) {
                t.toString() + "\n" + t.suppressed.map { "    $it" }.joinToString("\n", "", "")
            } else {
                t.toString()
            }
            feedbacks.add("Error occurred applying request ${k.player}(${k.requestId}):\n$exMessage")
        }
    }
    if (feedbacks.isNotEmpty()) {
        commandContext.sendFeedback(feedbacks.joinToString("\n", "", ""))
    }
    PendingRequests.whitelistRequests.removeAll(removes)
}

fun Config.getOperators(group: Long): List<Long>? {
    return this.groupSettings[group.toString()]?.operators
}
