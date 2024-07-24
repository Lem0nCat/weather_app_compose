package ru.lemoncat.weatherappcompose

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import ru.lemoncat.weatherappcompose.data.WeatherModel
import ru.lemoncat.weatherappcompose.screens.DialogSearch
import ru.lemoncat.weatherappcompose.screens.MainCard
import ru.lemoncat.weatherappcompose.screens.TabLayout
import ru.lemoncat.weatherappcompose.ui.theme.WeatherAppComposeTheme
import java.text.SimpleDateFormat

const val API_KEY = "your_api_key"
const val WEATHER_UNITS = "metric"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppComposeTheme {
                val searchActive = remember {   // Активно ли окно с поиском
                    mutableStateOf(false)
                }
                val weatherList = remember {    // Список погодных условий по часам (раз в 3 часа)
                    mutableStateOf(listOf<WeatherModel>())
                }
                val currentWeather = remember { // Объект погодных условий текущего времени
                    mutableStateOf(
                        WeatherModel(
                            "", "00.00", "00:00", "0.0", "0.0",
                            "0.0", "", "", ""
                        )
                    )
                }

                // Получение из сохраненных данных название города и выведение погоды на их основе
                val pref = getSharedPreferences("TABLE", MODE_PRIVATE)
                var cityName = pref.getString("savedCityName", "")!!
                if(cityName.isNotEmpty()) getData(cityName, this, weatherList, currentWeather)

                // При нажатии на кнопку меняется значение, а значит нужно вызывать DialogSearch
                if (searchActive.value)
                    DialogSearch(searchActive, onSubmit = { // Событие при нажатии на "ОК"
                        val editor: SharedPreferences.Editor = pref.edit()

                        // Убираем лишние отступы и записываем значение в переменную
                        cityName = it.trim()
                        editor.putString("savedCityName", cityName) // Запись города в файлы
                        editor.apply()

                        // Обновляем данные о погоде после изменения города
                        getData(cityName, this, weatherList, currentWeather)
                    })

                Image(  // Задний фон приложения
                    painter = painterResource(id = R.drawable.weather_background),
                    contentDescription = "im1",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column {    // Столбец, в котором вызывается основная и нижняя часть
                    MainCard(currentWeather, onClickSync = {
                        getData(cityName, this@MainActivity, weatherList, currentWeather)
                    }, onClickSearch = {
                        searchActive.value = true
                    })
                    TabLayout(weatherList)
                }
            }
        }
    }
}

// Функция, которая получает значения текущей погоды и погоды на 5 дней по часам
private fun getData(
    city: String, context: Context,
    weatherList: MutableState<List<WeatherModel>>,
    currentWeather: MutableState<WeatherModel>
) {
    val forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?q=$city" +
            "&units=$WEATHER_UNITS" +
            "&appid=$API_KEY"
    val weatherUrl = forecastUrl.replace("forecast", "weather")
    val queue = Volley.newRequestQueue(context)

    // Отправка первого запроса на получение погоды на 5 дней почасово
    val forecastRequest = StringRequest(
        Request.Method.GET,
        forecastUrl,
        { response ->
            val list = getWeekWeatherData(response)
            weatherList.value = list
            Log.d("MyLog", "Response: $list")
        },
        {
            Log.d("MyLog", "VolleyError: $it")
            Toast.makeText(context, "City name error!", Toast.LENGTH_SHORT).show()
        }
    )
    queue.add(forecastRequest)

    // Отправка второго запроса на получение текущей погоды
    val weatherRequest = StringRequest(
        Request.Method.GET,
        weatherUrl,
        { response ->
            val currentWeatherData = getCurrentWeatherData(response)
            if (currentWeatherData != null) {
                currentWeather.value = currentWeatherData
            } else {
                Log.d("MyLog", "VolleyError: An empty request was received!")
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
            }
        }, {
            Log.d("MyLog", "VolleyError: $it")
        })
    queue.add(weatherRequest)
}

// Функция, которая извлекает данные текущей погоды из ответа от api
private fun getCurrentWeatherData(response: String): WeatherModel? {
    if (response.isEmpty()) return null // Если

    val mainObject = JSONObject(response)

    // Получение названия города
    val city = mainObject.getString("name")

    // Получение значений температур
    val temp = mainObject.getJSONObject("main").getString("temp")
    val maxTemp = mainObject.getJSONObject("main").getString("temp_max")
    val minTemp = mainObject.getJSONObject("main").getString("temp_min")

    // Получение погодных условий
    val weatherObject = mainObject.getJSONArray("weather")[0] as JSONObject
    val weatherName = weatherObject.getString("main")
    val description = weatherObject.getString("description")
    val icon = weatherObject.getString("icon")

    // Возвращение в виде объекта WeatherModel
    return WeatherModel(
        city, "", "", temp, maxTemp, minTemp, weatherName, description, icon
    )
}

// Функция, которая извлекает данные погоды на 5 дней из ответа от api
@SuppressLint("SimpleDateFormat")
private fun getWeekWeatherData(response: String): List<WeatherModel> {
    // Если ничего не возвращается, то возвращаем пустой список
    if (response.isEmpty()) return emptyList()

    val list = ArrayList<WeatherModel>()

    val mainObject = JSONObject(response)

    // Получение названия города
    val city = mainObject.getJSONObject("city").getString("name")

    // Получение списка погодных условий по часам
    val hours = mainObject.getJSONArray("list")

    // Цикл, который пробегается по списку hours
    for (i in 0 until hours.length()) {
        val item = hours[i] as JSONObject
        val weather = item.getJSONArray("weather")[0] as JSONObject

        // Форматирование строки даты и времени
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateTime = dateFormat.parse(item.getString("dt_txt"))

        // Получение значения даты в формате "dd.MM.yyyy"
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
        val dateResult = dateTime?.let { dateFormatter.format(it) }

        // Получение значения времени в формате "HH:mm"
        val timeFormatter = SimpleDateFormat("HH:mm")
        val timeResult = dateTime?.let { timeFormatter.format(it) }


        list.add(   // Добавление каждого элемента часа погоды в список в виде объектов WeatherModel
            WeatherModel(
                city = city,
                date = dateResult.toString(),
                time = timeResult.toString(),
                currentTemp = item.getJSONObject("main").getString("temp"),
                maxTemp = "", minTemp = "",
                weatherName = weather.getString("main"),
                description = weather.getString("description"),
                iconName = weather.getString("icon")
            )
        )
    }
    return list
}
