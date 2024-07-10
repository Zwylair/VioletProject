package cardControl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import java.sql.Connection

fun unblockCardsInThisChat(bot: Bot, message: Message, conn: Connection, update: Update) {
    val textContent = message.text ?: return
    if (!textContent.startsWith("${Config.BOT_PREFIX}unblockcards")) return
    update.consume()

    val chatId = ChatId.fromId(message.chat.id)
    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatMembers = bot.getChatAdministrators(chatId).get()
    if (!chatMembers.contains(chatMember)) { return }

    if (!isChatIdCachedOrInDatabase(conn, chatId.id)) {
        bot.sendMessage(
            chatId,
            text = "Cards are not blocked in this chat!",
            replyToMessageId = message.messageId
        )
        return
    }

    removeCardBlockChat(conn, chatId.id)
    bot.sendMessage(
        chatId,
        text = "Cards were successfully unblocked in this chat!",
        replyToMessageId = message.messageId
    )
}
