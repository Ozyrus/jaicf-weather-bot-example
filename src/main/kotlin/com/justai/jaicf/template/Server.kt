package com.justai.jaicf.template

import com.justai.jaicf.channel.googleactions.ActionsFulfillment
import com.justai.jaicf.channel.http.httpBotRouting
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import com.justai.jaicf.channel.telegram.TelegramChannel

fun main() {
//    embeddedServer(Netty, 8080) {
//        routing {
//            httpBotRouting(
//                "/" to ActionsFulfillment.dialogflow(templateBot)
//            )
//        }
//    }.start(wait = true)
    TelegramChannel(templateBot, "1106535183:AAFAJY8wr5tkV9t7E4BTct8WbbZ2BGaiLXk").run()
}