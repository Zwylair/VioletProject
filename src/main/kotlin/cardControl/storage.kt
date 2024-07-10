package cardControl

import Action
import isResponseEmpty
import java.sql.Connection

val chatIdCache = mutableSetOf<Long>()

fun isChatIdCachedOrInDatabase(dbConnection: Connection, chatId: Long): Boolean {
    if (chatId in chatIdCache) return true

    val query = "SELECT * FROM cardsBlock WHERE chatId=?"
    val statement = dbConnection.prepareStatement(query)
    statement.setObject(1, chatId)
    val resSet = statement.executeQuery()
    val exists = !isResponseEmpty(resSet)

    if (exists) chatIdCache.add(chatId)

    resSet.close()
    statement.close()

    return exists
}

fun addCardBlockChat(dbConnection: Connection, chatId: Long, parsedAction: Action) {
    val statement = dbConnection.prepareStatement("INSERT OR IGNORE INTO cardsBlock VALUES (?, ?, ?, ?)")
    statement.setObject(1, chatId)
    statement.setObject(2, parsedAction.action)
    statement.setObject(3, parsedAction.restrictTime)
    statement.setObject(4, parsedAction.reason)
    statement.executeUpdate()
    statement.close()

    chatIdCache.add(chatId)
}

fun removeCardBlockChat(dbConnection: Connection, chatId: Long) {
    val statement = dbConnection.prepareStatement("DELETE FROM cardsBlock WHERE chatId=?")
    statement.setObject(1, chatId)
    statement.executeUpdate()
    statement.close()

    chatIdCache.remove(chatId)
}
