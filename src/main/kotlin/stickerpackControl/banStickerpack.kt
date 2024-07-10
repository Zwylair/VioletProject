package stickerpackControl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import java.sql.Connection
import isResponseEmpty
import customDurationToUnix

fun banStickerPack(bot: Bot, message: Message, conn: Connection, update: Update) {
    val textContent = message.text ?: return
    if (!textContent.startsWith("${Config.BOT_PREFIX}banstickerpack")) return
    update.consume()

    val chatId = ChatId.fromId(message.chat.id)
    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatMembers = bot.getChatAdministrators(chatId).get()
    if (!chatMembers.contains(chatMember)) { return }

    val replyMessage = message.replyToMessage
    if (replyMessage?.sticker == null) {
        bot.sendMessage(
            chatId,
            text = "Reply to the sticker for banning the pack!",
            replyToMessageId = message.messageId
        )
        return
    }

    val packName = replyMessage.sticker!!.setName
    val statement = conn.prepareStatement("SELECT * FROM stickerPackBanList WHERE packName=? AND chatId=?")
    statement.setObject(1, packName)
    statement.setObject(2, chatId.id)
    val resSet = statement.executeQuery()

    if (!isResponseEmpty(resSet)) {
        bot.sendMessage(
            chatId,
            text = "This stickerpack already banned!",
            replyToMessageId = message.messageId
        )
        return
    }

    val possibleActions = listOf("delete", "ban", "kick", "mute", "sban", "skick", "smute")
    val doAction = textContent.split("do=")
    var action: String? = null
    var parsedTime: Long? = null
    var reason: String? = null

    when {
        doAction.size > 2 -> {
            bot.sendMessage(
                chatId,
                text = "\"do=\" sentence is invalid! (There are some \"do=\" sentences)",
                replyToMessageId = message.messageId
            )
            return
        }
        doAction.size == 2 -> {
            try {
                val doThing = doAction[1].split(" ")
                action = doThing[0]
                parsedTime = customDurationToUnix(doThing.getOrNull(1))
                reason = doThing.getOrNull(2)

                if (!possibleActions.contains(action)) {
                    bot.sendMessage(
                        chatId,
                        text = "\"do=\" sentence is invalid! There is no \"$action\" action (${possibleActions.joinToString(", ")})",
                        replyToMessageId = message.messageId
                    )
                    return
                }
            } catch (e: IndexOutOfBoundsException) {
                bot.sendMessage(
                    chatId,
                    text = "\"do=\" sentence needs to contain at least 1 argument! (action, [time], [reason])",
                    replyToMessageId = message.messageId
                )
                return
            }
        }
        doAction.size == 1 -> {
            action = "delete"
        }
    }

    val seq = conn.prepareStatement("INSERT INTO stickerPackBanList VALUES (?, ?, ?, ?, ?)")
    seq.setObject(1, packName)
    seq.setObject(2, chatId.id)
    seq.setObject(3, action)
    seq.setObject(4, parsedTime)
    seq.setObject(5, reason)
    seq.executeUpdate()

    bot.sendMessage(
        chatId,
        text = "This stickerpack has been successfully banned!",
        replyToMessageId = message.messageId
    )
}
