package moderator

import Action
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message

fun autoRestrictAction(bot: Bot, message: Message, parsedAction: Action) {
    val action = parsedAction.action
    val restrictTime = parsedAction.restrictTime
    val reason = parsedAction.reason

    when (action) {
        "ban" -> banUser(bot, message, restrictTime, reason)
        "mute" -> muteUser(bot, message, restrictTime, reason)
        "kick" -> kickUser(bot, message, reason)
        "delete" -> { bot.deleteMessage(ChatId.fromId(message.chat.id), message.messageId) }
        "sban" -> banUser(bot, message, restrictTime, reason, silent = true)
        "smute" -> muteUser(bot, message, restrictTime, reason, silent = true)
        "skick" -> kickUser(bot, message, reason, silent = true)
    }
}
