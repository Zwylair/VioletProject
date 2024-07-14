import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Config {
    lateinit var BOT_TOKEN: String
    lateinit var PAYMENT_TOKEN: String
    const val DB_PATH = "database.sqlite"
    const val BOT_PREFIX = "-"
    val LOGGER: Logger = LoggerFactory.getLogger("VioletProject")
}
