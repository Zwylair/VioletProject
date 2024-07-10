import Config.BOT_PREFIX
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import notes.*
import stickerpackControl.*
import cardControl.*

object BotCommand : Filter {
    override fun Message.predicate(): Boolean = (text ?: "").startsWith(BOT_PREFIX)
}

fun main(vararg args: String) {
    val bot = bot {
        token = args.first()
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

        dispatch {
            // notes
            message(Filter.Group) { addNote(bot, message, conn, update) }
            message(Filter.Group and BotCommand) { removeNote(bot, message, conn, update) }

            // sticker pack control
            message(Filter.Group and BotCommand) { banStickerPack(bot, message, conn, update) }
            message(Filter.Group and BotCommand) { unbanStickerPack(bot, message, conn, update) }

            // cards control
            message(Filter.Group and BotCommand) { blockCardsInThisChat(bot, message, conn, update) }
            message(Filter.Group and BotCommand) { unblockCardsInThisChat(bot, message, conn, update) }

            // message handlers
            message(Filter.Group and Filter.Sticker) { stickerHandler(bot, message, conn, update) }
            message(Filter.Group and Filter.Text) { noteTextHandler(bot, message, conn, update) }
            message(Filter.Group and Filter.Text) { cardTextHandler(bot, message, conn, update) }

            addErrorHandler(ErrorHandler { Exception(error.getErrorMessage()).printStackTrace() })
        }
    }

    val me = bot.getMe().get()

    println("@${me.username} [${me.id}] started")
    bot.startPolling()
}
