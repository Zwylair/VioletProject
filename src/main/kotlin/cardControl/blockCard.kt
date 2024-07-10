package cardControl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import parseDoAction
import java.sql.Connection

fun blockCardsInThisChat(bot: Bot, message: Message, conn: Connection, update: Update) {
    val textContent = message.text ?: return
    if (!textContent.startsWith("${Config.BOT_PREFIX}blockcards")) return
    update.consume()

    val chatId = ChatId.fromId(message.chat.id)
    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatMembers = bot.getChatAdministrators(chatId).get()
    if (!chatMembers.contains(chatMember)) { return }

    if (isChatIdCachedOrInDatabase(conn, chatId.id)) {
        bot.sendMessage(
            chatId,
            text = "Cards are already blocked in this chat!",
            replyToMessageId = message.messageId
        )
        return
    }

    val parsedAction = parseDoAction(textContent, bot, message)
    if (parsedAction.isEmpty()) { return }

    addCardBlockChat(conn, chatId.id, parsedAction)
    bot.sendMessage(
        chatId,
        text = "Cards were successfully blocked in this chat!",
        replyToMessageId = message.messageId
    )
}
