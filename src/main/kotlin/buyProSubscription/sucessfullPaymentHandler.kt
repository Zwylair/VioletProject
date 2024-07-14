package buyProSubscription

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import java.sql.Connection

fun successfulProSubPayment(bot: Bot, message: Message, conn: Connection, update: Update) {
    val chatId = ChatId.fromId(message.chat.id)
    val successfulPayment = update.message?.successfulPayment!!

    if (successfulPayment.invoicePayload != Definitions.PAYLOAD) { return }
    update.consume()

    val statement = conn.prepareStatement("INSERT INTO proUsers VALUES (?, ?, ?, ?)")
    statement.setObject(1, chatId.id)
    statement.setObject(2, System.currentTimeMillis())
    statement.setObject(3, successfulPayment.totalAmount)
    statement.setObject(4, successfulPayment.telegramPaymentChargeId)
    statement.executeUpdate()

    val text = """
        Payment received: ${successfulPayment.totalAmount / 100.0} ${successfulPayment.currency} 💵
        Your pro subscription was successfully activated ❤️

        Receipt ID (you can use this ID to prove payment to the support): <code>${successfulPayment.telegramPaymentChargeId}</code>
    """.trimIndent()

    bot.sendMessage(chatId, text, parseMode = ParseMode.HTML).fold({}, {})
}
