import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import java.time.Duration
import java.util.concurrent.TimeUnit

data class Action(val action: String, val restrictTime: Long?, val reason: String?) {
    companion object {
        fun empty(): Action {
            return Action("", null, null)
        }
    }

    fun isEmpty(): Boolean {
        return (action == "" && restrictTime == null && reason == null)
    }
}

fun customDurationToUnix(durationStr: String?): Long? {
    if (durationStr == null) { return null }

    val pattern = "(\\d+)([dhms])".toRegex()
    var duration = Duration.ZERO

    pattern.findAll(durationStr).forEach { matchResult ->
        val value = matchResult.groupValues[1].toLong()
        val unit = matchResult.groupValues[2]

        duration = when (unit) {
            "d" -> duration.plusDays(value)
            "h" -> duration.plusHours(value)
            "m" -> duration.plusMinutes(value)
            "s" -> duration.plusSeconds(value)
            else -> throw IllegalArgumentException("Unknown time unit: $unit")
        }
    }

    return duration.toMillis() / 1000L
}

fun parseUnixToReadable(timestamp: Long?): String? {
    if (timestamp == null) { return null }

    val days = TimeUnit.SECONDS.toDays(timestamp)
    val hours = TimeUnit.SECONDS.toHours(timestamp) % 24
    val minutes = TimeUnit.SECONDS.toMinutes(timestamp) % 60
    val seconds = timestamp % 60

    val parts = mutableListOf<String>()
    if (days > 0) { parts.add("$days days") }
    if (hours > 0) { parts.add("$hours hours") }
    if (minutes > 0) { parts.add("$minutes minutes") }
    if (seconds > 0) { parts.add("$seconds seconds") }

    return parts.joinToString(", ")
}

fun parseDoAction(rawCommand: String, bot: Bot, message: Message): Action {
    val possibleActions = listOf("delete", "ban", "kick", "mute", "sban", "skick", "smute")
    val chatId = ChatId.fromId(message.chat.id)
    val doAction = rawCommand.split("do=")
    var action = "delete"
    var parsedTime: Long? = null
    var reason: String? = null

    when {
        doAction.size > 2 -> {
            bot.sendMessage(
                chatId,
                text = "\"do=\" sentence is invalid! (There are some \"do=\" sentences)",
                replyToMessageId = message.messageId
            )
            return Action.empty()
        }
        doAction.size == 2 -> {
            try {
                val doThing = doAction[1].split(" ")
                action = doThing[0]
                parsedTime = customDurationToUnix(doThing.getOrNull(1))
                reason = doThing.getOrNull(2)

                if (!possibleActions.contains(action)) {
                    bot.sendMessage(
                        chatId,
                        text = "\"do=\" sentence is invalid! There is no \"$action\" action (${possibleActions.joinToString(", ")})",
                        replyToMessageId = message.messageId
                    )
                    return Action.empty()
                }
            } catch (e: IndexOutOfBoundsException) {
                bot.sendMessage(
                    chatId,
                    text = "\"do=\" sentence needs to contain at least 1 argument! (action, [time], [reason])",
                    replyToMessageId = message.messageId
                )
                return Action.empty()
            }
        }
        doAction.size == 1 -> {
            action = "delete"
        }
    }

    return Action(action, parsedTime, reason)
}
