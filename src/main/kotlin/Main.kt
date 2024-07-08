import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.extensions.filters.Filter

fun main(vararg args: String) {
    val bot = bot {
        token = args.first()
        val conn = connect(Config.DB_PATH)
        val statement = conn.createStatement()

        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS stickerPackBanList (
                packName TEXT, 
                chatId INT, 
                action TEXT, 
                restrictTime INT, 
                reason TEXT
            )
        """.trimIndent())
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS notes (
                noteName TEXT, 
                noteText TEXT, 
                chatId INT, 
                mediaFileId TEXT, 
                mediaType TEXT
            )
        """.trimIndent())

        dispatch {
            message(Filter.Group) { notes.addNote(bot, message, conn, update) }
            message(Filter.Group) { notes.removeNote(bot, message, conn, update) }
            message(Filter.Group and Filter.Reply) { stickerpackControl.banStickerPack(bot, message, conn, update) }
            message(Filter.Group and Filter.Reply) { stickerpackControl.unbanStickerPack(bot, message, conn, update) }
            message(Filter.Group and Filter.Sticker) { stickerpackControl.stickerHandler(bot, message, conn, update) }
            message(Filter.Group and Filter.Text) { notes.noteTextHandler(bot, message, conn, update) }
        }
    }

    val me = bot.getMe().get()

    println("@${me.username} [${me.id}] started")
    bot.startPolling()
}
