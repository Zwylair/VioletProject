package moderator

import checkCanDeleteMessages
import checkCanRestrict
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import parseUnixToReadable

fun banUser(bot: Bot, message: Message, restrictTime: Long?, reason: String?, silent: Boolean = false) {
    val notEnoughRestrictTime = if ((restrictTime?: 0) < 30) { 30 - (restrictTime?: 0) } else { restrictTime!! }
    val chatId = ChatId.fromId(message.chat.id)
    val member = message.from!!
    val notifyText = listOf(
        "Bye-bye!",
        "<a href=\"tg://user?id=${member.id}\">${member.firstName}</a> was banned" +
                parseUnixToReadable(restrictTime)?.let { " for $it" } +
                ".",
        if (reason == null) "" else "Reason:\n$reason"
    ).joinToString("\n")

    if (!checkCanDeleteMessages(bot, message)) return
    if (!checkCanRestrict(bot, message)) return

    bot.deleteMessage(chatId, message.messageId)
    bot.banChatMember(
        chatId,
        member.id,
        untilDate = restrictTime?.let { System.currentTimeMillis() / 1000L + restrictTime + notEnoughRestrictTime }
    )
    if (!silent) { bot.sendMessage(chatId, text = notifyText, parseMode = ParseMode.HTML) }
}
