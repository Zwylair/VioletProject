import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.preCheckoutQuery
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import Config.BOT_PREFIX
import notes.*
import cardControl.*
import stickerpackControl.*
import buyProSubscription.*
import com.github.kotlintelegrambot.logging.LogLevel

object BotCommand : Filter {
    override fun Message.predicate() = (text ?: "").startsWith(BOT_PREFIX)
}

object Payment : Filter {
    override fun Message.predicate() = successfulPayment != null
}

fun main(vararg botArgs: String) {
    Config.BOT_TOKEN = botArgs[0]
    Config.PAYMENT_TOKEN = botArgs[1]

    val bot = bot {
        token = Config.BOT_TOKEN
        logLevel = LogLevel.Error

        val conn = connect(Config.DB_PATH)
        val statement = conn.createStatement()

        statement.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS stickerPackBanList (
                packName TEXT,
                chatId INT,
                action TEXT,
                restrictTime INT,
                reason TEXT
            )
        """.trimIndent()
        )
        statement.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS notes (
                noteName TEXT,
                noteText TEXT,
                chatId INT,
                mediaFileId TEXT,
                mediaType TEXT
            )
        """.trimIndent()
        )
        statement.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS cardsBlock (
                chatId INT,
                action TEXT,
                restrictTime INT,
                reason TEXT
            )
        """.trimIndent()
        )
        statement.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS proUsers (
                userId INT,
                buyUnixTime TEXT,
                centsAmount INT,
                receiptId TEXT
            )
        """.trimIndent()
        )

        dispatch {
            // notes
            message(Filter.Group) { addNote(bot, message, conn, update) }
            message(Filter.Group and BotCommand) { removeNote(bot, message, conn, update) }
            message(Filter.Group and Filter.Text) { noteTextHandler(bot, message, conn, update) }

            // sticker pack control
            message(Filter.Group and BotCommand) { banStickerPack(bot, message, conn, update) }
            message(Filter.Group and BotCommand) { unbanStickerPack(bot, message, conn, update) }
            message(Filter.Group and Filter.Sticker) { stickerHandler(bot, message, conn, update) }

            // cards control
            message(Filter.Group and BotCommand) { blockCardsInThisChat(bot, message, conn, update) }
            message(Filter.Group and BotCommand) { unblockCardsInThisChat(bot, message, conn, update) }
            message(Filter.Group and Filter.Text) { cardTextHandler(bot, message, conn, update) }

            // buy pro subscription
            message(Filter.Private and BotCommand) { buyProSubscription(bot, message, conn, update) }
            message(Payment) { successfulProSubPayment(bot, message, conn, update) }
            command("start") { proSubStartCommandHandler(bot, message, conn, update, args) }

            preCheckoutQuery {
                val preCheckoutQuery = update.preCheckoutQuery ?: return@preCheckoutQuery
                bot.answerPreCheckoutQuery(
                    preCheckoutQueryId = preCheckoutQuery.id,
                    ok = true
                ).fold( { }, { println("Error answering pre-checkout query: $it") } )
            }
        }
    }

    val me = bot.getMe().get()

    println("@${me.username} [${me.id}] started")
    bot.startPolling()
}
