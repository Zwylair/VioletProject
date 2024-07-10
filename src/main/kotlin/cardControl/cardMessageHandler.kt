package cardControl

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import Action
import checkCanDeleteMessages
import checkCanRestrict
import moderator.autoRestrictAction
import java.sql.Connection

fun cardTextHandler(bot: Bot, message: Message, conn: Connection, update: Update) {
    val cardRegex = Regex("""\b\d{4}[^\wа-я]*\d{4}[^\wа-я]*\d{4}[^\wа-я]*\d{4}\b""")
    val chatId = ChatId.fromId(message.chat.id)

    if (!isChatIdCachedOrInDatabase(conn, chatId.id)) return

    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatAdmins = bot.getChatAdministrators(chatId).get()

    if (chatAdmins.contains(chatMember)) { return }
    if (!cardRegex.containsMatchIn(message.text ?: message.caption ?: ""))  return
    update.consume()

    checkCanDeleteMessages(bot, message)
    checkCanRestrict(bot, message)

    val statement = conn.prepareStatement("SELECT * FROM cardsBlock WHERE chatId=?")
    statement.setObject(1, chatId.id)
    val resSet = statement.executeQuery()
    val parsedAction = Action(
        action = resSet.getString("action"),
        restrictTime = resSet.getLong("restrictTime"),
        reason = resSet.getString("reason")
    )

    autoRestrictAction(bot, message, parsedAction)
}
