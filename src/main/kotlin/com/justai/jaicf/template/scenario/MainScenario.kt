package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.channel.googleactions.actions
import com.justai.jaicf.channel.googleactions.dialogflow.DialogflowIntent
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.template.City
import com.justai.jaicf.template.Weather
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.content

object MainScenario : Scenario() {

    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false, encodeDefaults = false))
    private val httpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    init {
        state("main") {
            activators {
                regex("/start")
            }

            action {
                reactions.say("Это бот который дает прогноз погоды в твоем городе. Просто спроси его.")
            }
        }

        state("hey") {
            activators {
                intent("Привет")
            }

            action {
                reactions.say("Привет, котик")
            }
        }
        state("bye") {
            activators {
                intent("Пока")
            }

            action {
                reactions.say("Пока, котик")
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
                    val res: JsonObject = runBlocking {
                        httpClient.get("http://api.openweathermap.org/data/2.5/weather?APPID=1955eacf9da35a2c323eb7c353e2a9c2&units=metric&lat=${lat}&lon=${lon}")
                    }
                    val weather = res.get("main")?.jsonObject?.get("temp")?.content
                    reactions.say("В $name сейчас температура $weather градусов")
                }
            }
        }

        state("fallback", noContext = true) {
            activators {
                catchAll()
            }

            action {
                reactions.say("This is a global catchAll")
                reactions.actions?.run {
                    say("Bye bye!")
                    endConversation()
                }
            }
        }
    }
}