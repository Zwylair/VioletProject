package notes

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.network.fold
import com.github.kotlintelegrambot.entities.*
import java.sql.Connection
import Config.LOGGER
import isResponseEmpty

fun noteTextHandler(bot: Bot, message: Message, conn: Connection, update: Update) {
    val chatId = ChatId.fromId(message.chat.id)

    try {
        val textContent = message.text!!
        if (!textContent.startsWith("#")) { return }
        update.consume()

        var replyToMessageId = message.messageId
        var deleteParentMessage = false

        if (message.replyToMessage != null) {
            replyToMessageId = message.replyToMessage?.messageId!!
            deleteParentMessage = true
        }

        val noteName = textContent.substring(1).lowercase()
        val statement = conn.prepareStatement("SELECT * FROM notes WHERE noteName=? AND chatId=?")
        statement.setObject(1, noteName)
        statement.setObject(2, chatId.id)
        val resSet = statement.executeQuery()

        if (isResponseEmpty(resSet)) {
            bot.sendMessage(
                chatId,
                text = "Note `#$noteName` was not found!",
                replyToMessageId = message.messageId,
                parseMode = ParseMode.MARKDOWN_V2
            )
            return
        }
        val noteText = resSet.getString("noteText") ?: ""
        val mediaFileId = resSet.getString("mediaFileId")
        val mediaType = resSet.getString("mediaType")

        when (mediaType) {
            null -> {
                bot.sendMessage(
                    chatId,
                    text = noteText,
                    replyToMessageId = replyToMessageId
                )
            }
            "animation" -> {
                val res = bot.sendAnimation(
                    chatId = chatId,
                    animation = TelegramFile.ByFileId(mediaFileId),
                    replyToMessageId = replyToMessageId,
                    caption = noteText
                )
                res.fold(
                    response = { println("All ok") } ,
                    error = { LOGGER.error(it.errorBody?.string()) }
                )
            }
            "photo" -> {
                bot.sendPhoto(
                    chatId = chatId,
                    photo = TelegramFile.ByFileId(mediaFileId),
                    replyToMessageId = replyToMessageId,
                    caption = noteText
                )
            }
            "video" -> {
                val res = bot.sendAnimation(
                    chatId = chatId,
                    animation = TelegramFile.ByFileId(mediaFileId),
                    replyToMessageId = replyToMessageId,
                    caption = noteText
                )
                res.fold(
                    response = { println("All ok") } ,
                    error = { LOGGER.error(it.errorBody?.string()) }
                )
            }
        }

        if (deleteParentMessage) {
            val meAsChatMember = bot.getChatMember(chatId, bot.getMe().get().id).get()

            // meAsChatMember.canDeleteMessages is nullable Boolean, "==" will offset the nullable property
            if (meAsChatMember.canDeleteMessages == true) {
                bot.deleteMessage(chatId, message.messageId)
            }
        }

    } catch (e: Exception) {
        LOGGER.error("An error occurred: {}", e.message, e)
        bot.sendMessage(
            chatId,
            text = "Error: ${e.message}",
            replyToMessageId = message.messageId
        )
    }
}
