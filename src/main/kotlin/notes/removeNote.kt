package notes

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import java.sql.Connection
import Config.LOGGER

fun removeNote(bot: Bot, message: Message, conn: Connection) {
    val chatId = ChatId.fromId(message.chat.id)

    try {
        val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
        val chatMembers = bot.getChatAdministrators(chatId).get()
        if (!chatMembers.contains(chatMember)) { return }

        val textContent = message.text ?: message.caption ?: return
        if (!textContent.startsWith("${Config.BOT_PREFIX}removenote")) return

        val messageParts = textContent.split(" ")
        val noteName: String

        try {
            noteName = messageParts[1].lowercase()
        } catch (e: IndexOutOfBoundsException) {
            bot.sendMessage(
                chatId,
                text = "Note name is empty!",
                replyToMessageId = message.messageId
            )
            return
        }

        val statement = conn.prepareStatement("DELETE FROM notes WHERE noteName=? AND chatId=?")
        statement.setObject(1, noteName)
        statement.setObject(2, message.chat.id)
        statement.executeUpdate()

        bot.sendMessage(
            chatId,
            text = "Note #$noteName has been removed!",
            replyToMessageId = message.messageId
        )
    } catch (e: Exception) {
        LOGGER.error("An error occurred: {}", e.message, e)
        bot.sendMessage(
            chatId,
            text = "Error: ${e.message}",
            replyToMessageId = message.messageId
        )
    }
}
