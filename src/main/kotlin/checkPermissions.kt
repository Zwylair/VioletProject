import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message

fun checkCanRestrict(bot: Bot, message: Message): Boolean {
    val chatId = ChatId.fromId(message.chat.id)
    val meAsChatMember = bot.getChatMember(chatId, bot.getMe().get().id).get()

    if (!meAsChatMember.canRestrictMembers!!) {
        bot.sendMessage(
            chatId,
            text = "I have no canRestrictMembers permission!",
            replyToMessageId = message.messageId
        )
        return false
    }
    return true
}

fun checkCanDeleteMessages(bot: Bot, message: Message): Boolean {
    val chatId = ChatId.fromId(message.chat.id)
    val meAsChatMember = bot.getChatMember(chatId, bot.getMe().get().id).get()

    if (!meAsChatMember.canDeleteMessages!!) {
        bot.sendMessage(
            chatId,
            text = "I have no canDeleteMessages permission!",
            replyToMessageId = message.messageId
        )
        return false
    }
    return true
}
