package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.channel.googleactions.actions
import com.justai.jaicf.channel.googleactions.dialogflow.DialogflowIntent
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.template.City
import com.justai.jaicf.template.Weather
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object MainScenario: Scenario() {

    private val json = Json(JsonConfiguration.Stable.copy( encodeDefaults = false))

    init {
        state("main") {
            activators {
                regex("/start")
            }

            action {
                reactions.say("Hi there mate!")
            }
        }

        state("fallback", noContext = true) {
            activators {
                catchAll()
            }

            action {
                reactions.say("This is a globat catchAll")
                reactions.actions?.run {
                    say("Bye bye!")
                    endConversation()
                }
            }
        }

        state("city") {
            activators {
                intent("Прогноз погоды")
            }
            action {
                activator.caila?.run {
                    val city = json.parse(City.serializer(), slots["Город"] ?: error("Got a Null"))
                    val lat = city.lat
                    val lon = city.lon
                    val name = city.name
                    val client = HttpClient()
                    val response =  runBlocking {client.get<String>("http://api.openweathermap.org/data/2.5/weather?APPID=1955eacf9da35a2c323eb7c353e2a9c2&units=metric&lat=${lat}&lon=${lon}")}
                    val weather = json.parse(Weather.serializer(), response)
                    val temperature = weather.temp
                reactions.say("В $name сейчас температура $temperature градусов")
                }
            }
        }
    }
}