package stickerpackControl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import java.sql.Connection
import isResponseEmpty
import moderator.banUser
import moderator.kickUser
import moderator.muteUser

fun stickerHandler(bot: Bot, message: Message, conn: Connection, update: Update) {
    message.sticker!!.setName ?: return

    val chatId = ChatId.fromId(message.chat.id)
    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatMembers = bot.getChatAdministrators(chatId).get()
    val meAsChatMember = bot.getChatMember(chatId, bot.getMe().get().id).get()

    if (chatMembers.contains(chatMember)) { return }
    if (!meAsChatMember.canDeleteMessages!!) {
        bot.sendMessage(
            chatId,
            text = "I have no canDeleteMessages permission!",
            replyToMessageId = message.messageId
        )
        return
    }

    val statement = conn.prepareStatement("SELECT * FROM stickerPackBanList WHERE packName=? AND chatId=?")
    statement.setObject(1, message.sticker!!.setName)
    statement.setObject(2, chatId.id)
    val resSet = statement.executeQuery()

    if (isResponseEmpty(resSet)) { return }
    val action = resSet.getString("action")
    val restrictTime = resSet.getLong("restrictTime")
    val reason = resSet.getString("reason")

    when (action) {
        "ban" -> banUser(bot, message, restrictTime, reason)
        "mute" -> muteUser(bot, message, restrictTime, reason)
        "kick" -> kickUser(bot, message, reason)
        "delete" -> {}
        "sban" -> banUser(bot, message, restrictTime, reason, silent = true)
        "smute" -> muteUser(bot, message, restrictTime, reason, silent = true)
        "skick" -> kickUser(bot, message, reason, silent = true)
    }

    bot.deleteMessage(chatId, message.messageId)
    update.consume()
}
