package org.zwylair.violetproject

import dev.inmo.micro_utils.coroutines.subscribeSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.*
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.usernameChatOrNull
import dev.inmo.tgbotapi.types.message.MarkdownV2
import kotlinx.coroutines.*
import kotlin.reflect.typeOf

suspend fun main(vararg args: String) {
    val botToken = args.first()

    telegramBotWithBehaviourAndLongPolling(botToken, CoroutineScope(Dispatchers.IO)) {
        val me = getMe()

        onCommand("start") { message ->
            val chat = message.chat
            sendMessage(chat, "blabla")

//            reply(
//                message,
//                "Hi",
//                MarkdownV2
//            )
        }

//        allUpdatesFlow.subscribeSafelyWithoutExceptions(this) { println(it) }
        println("${me.username} [${me.id.chatId}] started")
    }.second.join()
}
