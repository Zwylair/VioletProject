import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

fun isResponseEmpty(res: ResultSet): Boolean {
    return res.getObject(1) == null
}

fun connect(dbPath: String): Connection {
    val url = "jdbc:sqlite:$dbPath"
    return DriverManager.getConnection(url)
}
