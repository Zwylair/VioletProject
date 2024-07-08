package stickerpackControl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import java.sql.Connection
import isResponseEmpty

fun unbanStickerPack(bot: Bot, message: Message, conn: Connection, update: Update) {
    val textContent = message.text ?: return
    if (!textContent.startsWith("${Config.BOT_PREFIX}unbanstickerpack")) return
    update.consume()

    val chatId = ChatId.fromId(message.chat.id)
    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatMembers = bot.getChatAdministrators(chatId).get()
    if (!chatMembers.contains(chatMember)) { return }

    val replyMessage = message.replyToMessage
    if (replyMessage?.sticker == null) {
        bot.sendMessage(
            chatId,
            text = "Reply to the sticker for unbanning the pack!",
            replyToMessageId = message.messageId
        )
        return
    }

    val packName = replyMessage.sticker!!.setName
    val statement = conn.prepareStatement("SELECT * FROM stickerPackBanList WHERE packName=? AND chatId=?")
    statement.setObject(1, packName)
    statement.setObject(2, chatId.id)
    val resSet = statement.executeQuery()

    if (isResponseEmpty(resSet)) {
        bot.sendMessage(
            chatId,
            text = "This stickerpack is not banned!",
            replyToMessageId = message.messageId
        )
        return
    }

    val seq = conn.prepareStatement("DELETE FROM stickerPackBanList WHERE packName=? AND chatId=?")
    seq.setObject(1, packName)
    seq.setObject(2, chatId.id)
    seq.executeUpdate()

    bot.sendMessage(
        chatId,
        text = "This stickerpack has been successfully unbanned!",
        replyToMessageId = message.messageId
    )
}
