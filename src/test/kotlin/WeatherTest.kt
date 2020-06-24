import com.justai.jaicf.template.scenario.MainScenario
import com.justai.jaicf.test.ScenarioTest
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class WeatherTest: ScenarioTest(MainScenario) {

    @Test
    fun `Greets after start regex`() {
        query("/start") endsWithState "/main"
    }


    @Test
    fun `Gives you weather`() {
        query("Питер") endsWithState  "/city"
    }
}