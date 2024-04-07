package icu.takeneko.whitecat.mirai

import icu.takeneko.whitecat.mirai.command.CommandManager
import icu.takeneko.whitecat.mirai.data.Data
import icu.takeneko.whitecat.mirai.data.Data.config
import icu.takeneko.whitecat.mirai.data.PendingRequests
import icu.takeneko.whitecat.mirai.util.BuildProperties
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.action.Nudge.Companion.sendNudge
import net.mamoe.mirai.message.data.*

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "icu.takeneko.whitecat",
        name = "WhiteCat",
        version = BuildProperties["version"]!!
    )
) {

    private lateinit var bot: Bot

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        Data.loadAll()
        config.run {
            logger.info(
                """
            Config:
                - owner: $owner
                - allowOwnerFullAccess: $allowOwnerFullAccess
                - enabledIn: ${availableGroups.joinToString(", ", "", "")}
                - groupSettings: ${
                    groupSettings.entries.joinToString(separator = "\n", "", "") {
                        """
                        => Group: ${it.key}
                            - operators: ${it.value.operators.joinToString(", ", "", "")}
                            - allowedRequests: ${it.value.allowedRequests.joinToString(", ", "", "")}
                            - whitelistServiceProvider: ${it.value.whitelistServiceProvider}
                """.trimIndent()
                    }
                }
        """.trimIndent()
            )
        }
        val eventChannel = GlobalEventChannel.parentScope(this)

        eventChannel.subscribeAlways<GroupMessageEvent> {
            if (this.bot.id != config.get().botId) return@subscribeAlways
            launch {
                CommandManager.handleGroupMessage(it)
            }
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
            if (this.bot.id != config.get().botId) return@subscribeAlways
            launch {
                CommandManager.handleFriendMessage(it)
            }
        }
        eventChannel.subscribeAlways<BotOnlineEvent> {
            this@Plugin.bot = bot
        }
        val endTime = System.currentTimeMillis()
        logger.info("WhiteCat loaded!")
        val timeUsed = (endTime - startTime) / 1000f
        logger.info("Done(${timeUsed}s)! For help, type \"help\".")
    }

    override fun onDisable() {
        Data.saveAll()
    }

    fun respondInGroup(group: Group, member: Member, message: MessageChain) {
        launch {
            group.sendNudge(member.nudge())
            group.sendMessage(At(member) + message)
        }
    }

    fun respondToFriend(friend: Friend, message: MessageChain) {
        launch {
            friend.sendMessage(message)
        }
    }

    fun broadcast(member: List<User>, message: MessageChain) {
        launch {
            member.forEach {
                it.sendMessage(message)
            }
        }
    }

    fun broadcastToOpsInGroup(group: Group, message: MessageChain) {
        if (group.id in config.get().groupSettings.keys && group.id in config.get().availableGroups) {
            broadcast(
                config.get()
                    .groupSettings[group.id]!!
                    .operators.mapNotNull { group.getMember(it)?.asFriendOrNull() },
                message
            )
        }
    }

    fun sendAllPendingRequests(target: Friend){
        launch {
            val groups = (config.get().operators2GroupMap[target.id] ?: return@launch).map{ it.toString() }
            val requests = PendingRequests.whitelistRequests.get().filter { it.group in groups}
            val msg = buildMessageChain {
                add("=> Pending Requests\n")
                requests.groupBy { it.group }.forEach { t, u ->
                    add("=> From Group $t\n")
                    u.forEach {
                        add("${it.sourceDescriptor}(${it.source}) requested to ${it.operation.describe()} ${it.player}${if (it.targetDescriptor == null) "" else " into ${it.targetDescriptor}"}\n")
                    }
                }
            }
            target.sendMessage(msg)
        }
    }

    fun reportErrorToOwner(e: Throwable, t: Thread = Thread.currentThread()) {
        launch {
            bot.getFriend(config.get().owner)?.run {
                sendMessage("Exception in thread ${t.name}:\n${e.stackTraceToString()}")
            }
        }
    }
}