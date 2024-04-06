package icu.takeneko.whitecat.mirai.command

import icu.takeneko.whitecat.mirai.Plugin
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf

data class Context(
    val senderUin: Long,
    val sender: User,
    val messageChain: MessageChain,
    val groupUin: Long,
    val group: Group?
) {
    fun sendFeedback(s: String) {
        if (isFromGroup) {
            Plugin.respondInGroup(group!!, sender as Member, messageChainOf(PlainText(s)))
            return
        }
        Plugin.respondToFriend(sender as Friend, messageChainOf(PlainText(s)))
    }

    fun sendError(s: String) {
        if (isFromGroup) {
            Plugin.respondInGroup(group!!, sender as Member, messageChainOf(PlainText("E: "), PlainText(s)))
            return
        }
        Plugin.respondToFriend(sender as Friend, messageChainOf(PlainText("E: "), PlainText(s)))
    }

    val isFromGroup
        get() = group != null

    val isFromFriend
        get() = sender is Friend
}