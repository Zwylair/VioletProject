package notes

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import java.sql.Connection
import Config.LOGGER
import isResponseEmpty

fun addNote(bot: Bot, message: Message, conn: Connection) {
    val chatId = ChatId.fromId(message.chat.id)

    try {
        val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
        val chatMembers = bot.getChatAdministrators(chatId).get()
        if (!chatMembers.contains(chatMember)) { return }

        val textContent = message.text ?: message.caption ?: return
        if (!textContent.startsWith("${Config.BOT_PREFIX}addnote")) return

        val messageParts = textContent.split(" ")
        val noteText = messageParts.subList(2, messageParts.size).joinToString(" ")
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

        val statement = conn.prepareStatement("SELECT * FROM notes WHERE noteName=? AND chatId=?")
        statement.setObject(1, noteName)
        statement.setObject(2, message.chat.id)
        val resSet = statement.executeQuery()

        if (!isResponseEmpty(resSet)) {
            bot.sendMessage(
                chatId,
                text = "Note #$noteName already exists",
                replyToMessageId = message.messageId
            )
            return
        }

        var mediaFileId: String? = null
        var mediaType: String? = null

        if (message.animation != null) {
            mediaFileId = message.animation?.fileId!!
            mediaType = "animation"
        } else if (message.photo != null) {
            mediaFileId = message.photo?.get(0)?.fileId!!
            mediaType = "photo"
        } else if (message.video != null) {
            mediaFileId = message.video?.fileId!!
            mediaType = "video"
        }

        val seq = conn.prepareStatement("INSERT INTO notes VALUES (?, ?, ?, ?, ?)")
        seq.setObject(1, noteName)
        seq.setObject(2, noteText)
        seq.setObject(3, message.chat.id)
        seq.setObject(4, mediaFileId)
        seq.setObject(5, mediaType)
        seq.executeUpdate()

        bot.sendMessage(
            chatId,
            text = "Note #$noteName has been added!",
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
