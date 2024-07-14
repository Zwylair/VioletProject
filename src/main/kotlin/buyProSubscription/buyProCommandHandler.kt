package buyProSubscription

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.payments.PaymentInvoiceInfo
import java.sql.Connection
import isResponseEmpty

fun proSubStartCommandHandler(bot: Bot, message: Message, conn: Connection, update: Update, args: List<String>) {
    if (args.isNotEmpty() && args[0] == Definitions.START_PARAMETER) {
        buyProSubscription(bot, message, conn, update, fromStartCommand = true)
    }
}

fun buyProSubscription(bot: Bot, message: Message, conn: Connection, update: Update, fromStartCommand: Boolean = false) {
    val chatId = ChatId.fromId(message.chat.id)

    val textContent = message.text ?: message.caption ?: return
    if (!textContent.startsWith("${Config.BOT_PREFIX}subscribe") and !fromStartCommand) return
    update.consume()

    val statement = conn.prepareStatement("SELECT * FROM proUsers WHERE userId=?")
    statement.setObject(1, chatId.id)
    val resSet = statement.executeQuery()

    if (!isResponseEmpty(resSet)) {
        bot.sendMessage(chatId, "You already have pro subscription!")
        return
    }

    bot.sendInvoice(
        chatId,
        PaymentInvoiceInfo(
            title = "Pro subscription",
            description = "This subscription will unlock pro features for you",
            payload = Definitions.PAYLOAD,
            providerToken = Config.PAYMENT_TOKEN,
            startParameter = Definitions.START_PARAMETER,
            currency = "USD",
            prices = Definitions.PRICES
        )
    )
}
