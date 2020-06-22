package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.channel.googleactions.actions
import com.justai.jaicf.channel.googleactions.dialogflow.DialogflowIntent
import com.justai.jaicf.context.BotContext
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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.content

object MainScenario : Scenario() {

    class Weather(context: BotContext) {
        var lat: Double? by context.session
        var lon: Double? by context.session
        var res: JsonObject? by context.session
        var temperature: String? by context.session
        var weather: String? by context.session
        var name: String? by context.session
        var feelsLike: String? by context.session
    }

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
                var weather = Weather(context)
                activator.caila?.run {
                    val cityValue = json.parse(City.serializer(), slots["Город"] ?: error("Got a Null"))
                    weather.lat = cityValue.lat
                    weather.lon = cityValue.lon
                    weather.name = cityValue.name
                    weather.res = runBlocking {
                        httpClient.get<JsonObject>("http://api.openweathermap.org/data/2.5/onecall?APPID=1955eacf9da35a2c323eb7c353e2a9c2&units=metric&lat=${weather.lat}&lon=${weather.lon}&lang=ru")
                    }
                    weather.temperature = weather.res!!.get("current")?.jsonObject?.get("temp")?.content
                    weather.feelsLike = weather.res!!.get("current")?.jsonObject?.get("feels_like")?.content
                    weather.weather = weather.res!!.get("current")?.jsonObject?.get("weather")?.jsonArray?.get(0)?.jsonObject?.get("description")?.content
                    if (slots["день"] != null) reactions.say("Ты просил погоду на другой день, но я знаю только про сегодня.")
                    reactions.say("В городе ${weather.name} сейчас температура ${weather.temperature} градусов, ощущается как ${weather.feelsLike}, ${weather.weather}.")
                }
            }
        }

        state("fallback", noContext = true) {
            activators {
                catchAll()
            }

            action {
                reactions.say("Просто назови город, и я дам тебе прогноз на сегодня.")
                reactions.actions?.run {
                    say("Bye bye!")
                    endConversation()
                }
            }
        }
    }
}