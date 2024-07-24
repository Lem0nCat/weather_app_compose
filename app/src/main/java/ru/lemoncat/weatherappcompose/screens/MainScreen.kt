@file:OptIn(ExperimentalFoundationApi::class)

package ru.lemoncat.weatherappcompose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.lemoncat.weatherappcompose.R
import ru.lemoncat.weatherappcompose.data.WeatherModel
import ru.lemoncat.weatherappcompose.ui.theme.MainColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Шрифт
val fontFamily = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold),
)

@Composable
fun MainCard(   // Карточка, где показывается информация о текущей погоде
    currentWeatherData: MutableState<WeatherModel>,
    onClickSync: () -> Unit,
    onClickSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MainColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Кнопка с иконкой (Поиск)
                    IconButton(onClick = { onClickSearch.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "im3",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Получение текущей даты устройства
                    val currentDate = LocalDate.now()
                    // Форматирование текущей даты
                    val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val formattedDate = currentDate.format(dateFormat)
                    Text(
                        text = formattedDate,
                        style = TextStyle(fontSize = 18.sp, fontFamily = fontFamily),
                        color = Color.White
                    )

                    // Кнопка с иконкой (Обновление)
                    IconButton(onClick = { onClickSync.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "im4",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Text(
                    text = currentWeatherData.value.city,
                    style = TextStyle(fontSize = 25.sp, fontFamily = fontFamily),
                    color = Color.White
                )
                AsyncImage( // Иконка, которая скачивается по ссылке и выводится
                    model = "https://openweathermap.org/img/wn/" +
                            "${currentWeatherData.value.iconName}@4x.png",
                    contentDescription = "im2",
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "${currentWeatherData.value.currentTemp.toFloat().toInt()}°C",
                    style = TextStyle(
                        fontSize = 65.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Text(
                    text = currentWeatherData.value.weatherName,
                    style = TextStyle(fontSize = 22.sp, fontFamily = fontFamily),
                    color = Color.White
                )
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = "${currentWeatherData.value.maxTemp.toFloat().toInt()}°C" +
                            "/${currentWeatherData.value.minTemp.toFloat().toInt()}°C",
                    style = TextStyle(fontSize = 18.sp, fontFamily = fontFamily),
                    color = Color.White
                )
            }
        }
    }
}

// Нижняя часть приложения
@Composable
fun TabLayout(hourlyWeatherList: MutableState<List<WeatherModel>>) {
    val tabItems = listOf(  // Элементы вкладок
        TabItem(
            title = "Today",
            unselectedIcon = Icons.Outlined.CalendarToday,
            selectedIcon = Icons.Filled.CalendarToday
        ),
        TabItem(
            title = "Week",
            unselectedIcon = Icons.Outlined.CalendarMonth,
            selectedIcon = Icons.Filled.CalendarMonth
        )
    )

    var selectedTabIndex by remember {  // Индекс выбранной вкладки
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        tabItems.size
    }

    // При переключении вкладок меняется HorizontalPager
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            selectedTabIndex = selectedTabIndex,
            containerColor = MainColor
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = item.title,
                            color = Color.White,
                            style = TextStyle(fontFamily = fontFamily)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedTabIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
                .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            state = pagerState,
            userScrollEnabled = false
        ) { index ->
            val list = when (index) {
                1 -> groupByDays(hourlyWeatherList.value)
                else -> hourlyWeatherList.value.take(8)
            }
            MainList(list)
        }
    }
}

// Функция, которая определяет, является ли переданное время дневным
fun isDaytime(time: String): Boolean {
    val hour = time.split(":")[0].toInt()
    return hour in 9..15
}

// Функция, которая группирует список почасовых погодных условий по дням
private fun groupByDays(hourlyWeatherList: List<WeatherModel>): List<WeatherModel> {
    val resultList = ArrayList<WeatherModel>()
    val groupedList = hourlyWeatherList.groupBy { it.date } // Группировка

    // Цикл, который пробегает по группированному списку и высчитываем общие значения на период
    for ((date, models) in groupedList) {
        var maxTemp: Float = models[0].currentTemp.toFloat()
        var minTemp: Float = models[0].currentTemp.toFloat()
        var weatherName = ""
        var iconName = ""
        for (model in models) {
            // Нахождение минимальной температуры и максимальной
            if (model.currentTemp.toFloat() > maxTemp)
                maxTemp = model.currentTemp.toFloat()
            if (model.currentTemp.toFloat() < minTemp)
                minTemp = model.currentTemp.toFloat()
            // Поиск погоды днем, чтобы не показывать погоду ночью
            if (isDaytime(model.time) && weatherName.isEmpty()) {
                weatherName = model.weatherName
                iconName = model.iconName
            }
        }

        // Если в цикле не была найдена погода днем, то ставится ночная
        if (weatherName.isEmpty()) {
            weatherName = models[0].weatherName
            iconName = models[0].iconName
        }

        resultList.add( // Добавление в список погодных условий по дням
            WeatherModel(
                city = models[0].city,
                date = date, time = "",
                currentTemp = "",
                maxTemp = maxTemp.toString(), minTemp = minTemp.toString(),
                weatherName = weatherName, description = "",
                iconName = iconName
            )
        )
    }
    return resultList
}

// Data класс для элемента вкладок
data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)