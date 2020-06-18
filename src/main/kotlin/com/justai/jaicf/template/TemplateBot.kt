package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaIntentActivator
import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.channel.googleactions.dialogflow.ActionsDialogflowActivator
import com.justai.jaicf.context.manager.InMemoryBotContextManager
import com.justai.jaicf.context.manager.mongo.MongoBotContextManager
import com.justai.jaicf.template.scenario.MainScenario
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.activator.caila.dto.CailaIntent
import com.justai.jaicf.activator.regex.RegexActivator


private val contextManager = System.getenv("MONGODB_URI")?.let { url ->
    val uri = MongoClientURI(url)
    val client = MongoClient(uri)
    MongoBotContextManager(client.getDatabase(uri.database!!).getCollection("contexts"))

} ?: InMemoryBotContextManager

val CailaActivator = CailaIntentActivator.Factory(CailaNLUSettings("dd103d20-2fbe-489a-bf72-521a2a07708f",0.2))

val templateBot = BotEngine(
    model = MainScenario.model,
    contextManager = contextManager,
    activators = arrayOf(
        ActionsDialogflowActivator,
        CailaActivator,
        RegexActivator,
        CatchAllActivator
    )
)