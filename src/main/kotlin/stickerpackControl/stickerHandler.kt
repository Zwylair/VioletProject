package stickerpackControl

import Action
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import java.sql.Connection
import isResponseEmpty
import moderator.autoRestrictAction

fun stickerHandler(bot: Bot, message: Message, conn: Connection, update: Update) {
    message.sticker!!.setName ?: return

    val chatId = ChatId.fromId(message.chat.id)
    val chatMember = bot.getChatMember(chatId, message.from!!.id).get()
    val chatMembers = bot.getChatAdministrators(chatId).get()
    val meAsChatMember = bot.getChatMember(chatId, bot.getMe().get().id).get()

    if (chatMembers.contains(chatMember)) { return }
    if (!meAsChatMember.canDeleteMessages!!) {
        bot.sendMessage(
            chatId,
            text = "I have no canDeleteMessages permission!",
            replyToMessageId = message.messageId
        )
        return
    }

    val statement = conn.prepareStatement("SELECT * FROM stickerPackBanList WHERE packName=? AND chatId=?")
    statement.setObject(1, message.sticker!!.setName)
    statement.setObject(2, chatId.id)
    val resSet = statement.executeQuery()

    if (isResponseEmpty(resSet)) { return }
    val parsedAction = Action(
        action = resSet.getString("action"),
        restrictTime = resSet.getLong("restrictTime"),
        reason = resSet.getString("reason")
    )

    autoRestrictAction(bot, message, parsedAction)
    update.consume()
}
