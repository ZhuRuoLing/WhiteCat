package icu.takeneko.whitecat.mirai.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import icu.takeneko.whitecat.mirai.Plugin
import icu.takeneko.whitecat.mirai.data.Data.config
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.toMessageChain

object CommandManager {
    private val dispatcher = CommandDispatcher<Context>()

    init {
        dispatcher.register(helpCommand)
        dispatcher.register(whitelistCommand)
    }

    fun handleGroupMessage(event: GroupMessageEvent) {
        val ctx = Context(event.sender.id, event.sender, event.message.toMessageChain(), event.group.id, event.group)
        if (!event.message.contentToString().startsWith(config.get().commandPrefix))return
        val command = event.message.contentToString().removePrefix(config.get().commandPrefix)
        try {
            dispatcher.execute(command, ctx)
        } catch (_: CommandSyntaxException) {

        } catch (t: Throwable) {
            Plugin.reportErrorToOwner(t)
        }
    }

    fun handleFriendMessage(event: FriendMessageEvent) {
        val ctx = Context(event.sender.id, event.sender, event.message.toMessageChain(), 0, null)
        if (!event.message.contentToString().startsWith(config.get().commandPrefix))return
        val command = event.message.contentToString().removePrefix(config.get().commandPrefix)
        try {
            dispatcher.execute(command, ctx)
        } catch (_: CommandSyntaxException) {

        } catch (t: Throwable) {
            Plugin.reportErrorToOwner(t)
        }
    }
}

private fun CommandDispatcher<S>.register(helpCommand: LiteralCommand) {
    this.register(helpCommand.node)
}
