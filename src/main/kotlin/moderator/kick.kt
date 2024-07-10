package moderator

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode

fun kickUser(bot: Bot, message: Message, reason: String?, silent: Boolean = false) {
    val chatId = ChatId.fromId(message.chat.id)
    val meAsChatMember = bot.getChatMember(chatId, bot.getMe().get().id).get()
    val member = message.from!!
    val notifyText = listOf(
        "Bye-bye!",
        "<a href=\"tg://user?id=${member.id}\">${member.firstName}</a> was kicked.",
        if (reason == null) "" else "Reason:\n$reason"
    ).joinToString("\n")

    if (!meAsChatMember.canRestrictMembers!!) {
        bot.sendMessage(
            chatId,
            text = "I have no canRestrictMembers permission!",
            replyToMessageId = message.messageId
        )
        return
    }

    bot.unbanChatMember(chatId, member.id)
    if (!silent) { bot.sendMessage(chatId, text = notifyText, parseMode = ParseMode.HTML) }
}
