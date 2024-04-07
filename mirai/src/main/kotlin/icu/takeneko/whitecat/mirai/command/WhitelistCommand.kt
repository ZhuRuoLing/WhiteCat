package icu.takeneko.whitecat.mirai.command

import icu.takeneko.whitecat.mirai.Plugin
import icu.takeneko.whitecat.mirai.data.Config
import icu.takeneko.whitecat.mirai.data.Data.config
import icu.takeneko.whitecat.mirai.data.PendingRequests
import icu.takeneko.whitecat.mirai.data.RequestOps
import icu.takeneko.whitecat.mirai.data.WhitelistRequest
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.asFriendOrNull
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.contact.nameCardOrNick

val whitelistCommand = LiteralCommand("wl") {
    literal("req") {
        literal("a") {
            requires { this.isFromGroup && this.groupUin in config.get().availableGroups && this.groupUin in config.get().groupSettings.keys }
            wordArgument("playerName") {
                execute {
                    val playerName = getStringArgument("playerName")
                    PendingRequests.whitelistRequests.add(WhitelistRequest(
                        playerName,
                        RequestOps.ADD,
                        this.source.senderUin.toString(),
                        this.source.sender.nameCardOrNick,
                        this.source.groupUin.toString(),
                        this.source.group?.name ?: "unknown",
                        null
                    ))
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
                            if (serverTarget !in config.get().groupSettings[this.source.groupUin]!!.whitelistServiceProvider.whitelist.listAvailable()){
                                sendFeedback("$serverTarget not found in all ServerTargets")
                                return@execute 1
                            }
                            PendingRequests.whitelistRequests.add(WhitelistRequest(
                                playerName,
                                RequestOps.ADD,
                                this.source.senderUin.toString(),
                                this.source.sender.nameCardOrNick,
                                this.source.groupUin.toString(),
                                this.source.group?.name ?: "unknown",
                                serverTarget
                            ))
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
            requires { this.groupUin in config.get().availableGroups && this.groupUin in config.get().groupSettings.keys }
            wordArgument("playerName") {
                execute {
                    val playerName = getStringArgument("playerName")
                    PendingRequests.whitelistRequests.add(WhitelistRequest(
                        playerName,
                        RequestOps.REMOVE,
                        this.source.senderUin.toString(),
                        this.source.sender.nameCardOrNick,
                        this.source.groupUin.toString(),
                        this.source.group?.name ?: "unknown",
                        null
                    ))
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
                            if (serverTarget !in config.get().groupSettings[this.source.groupUin]!!.whitelistServiceProvider.whitelist.listAvailable()){
                                sendFeedback("$serverTarget not found in all ServerTargets")
                                return@execute 1
                            }
                            PendingRequests.whitelistRequests.add(WhitelistRequest(
                                playerName,
                                RequestOps.ADD,
                                this.source.senderUin.toString(),
                                this.source.sender.nameCardOrNick,
                                this.source.groupUin.toString(),
                                this.source.group?.name ?: "unknown",
                                serverTarget
                            ))
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
                0
            }
        }
    }
    literal("tgt") {
        requires { this.groupUin in config.get().availableGroups || this.senderUin in config.get().allOperators }
        execute {
            0
        }
    }
    literal("approve") {
        requires { this.senderUin in config.get().allOperators }
        wordArgument("playerName") {
            execute {
                val playerName = getStringArgument("playerName")

                0
            }
            literal("into") {
                wordArgument("serverTarget") {
                    execute {
                        0
                    }
                }
            }
        }
    }
}

fun Config.getOperators(group:Long):List<Long>?{
    return this.groupSettings[group]?.operators
}
