import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.extensions.filters.Filter

fun main(vararg args: String) {
    val bot = bot {
        token = args.first()
        val conn = connect(Config.DB_PATH)

        conn.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS notes (noteName TEXT, noteText TEXT, chatId INT, mediaFileId TEXT, mediaType TEXT)"
        )

        dispatch {
            message(Filter.Group) { notes.addNote(bot, message, conn) }
            message(Filter.Group) { notes.removeNote(bot, message, conn) }
            message(Filter.Group and Filter.Text) { notes.noteTextHandler(bot, message, conn) }
        }
    }

    val me = bot.getMe().get()

    println("@${me.username} [${me.id}] started")
    bot.startPolling()
}
