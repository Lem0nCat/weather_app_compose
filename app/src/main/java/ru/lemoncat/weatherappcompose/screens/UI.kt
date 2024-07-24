package ru.lemoncat.weatherappcompose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.lemoncat.weatherappcompose.data.WeatherModel
import ru.lemoncat.weatherappcompose.ui.theme.DarkBlue
import ru.lemoncat.weatherappcompose.ui.theme.MainColor


// Список с погодой на день/неделю
@Composable
fun MainList(list: List<WeatherModel>) {
    LazyRow(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            list
        ) { _, item -> ListItem(item) }
    }
}

// Элемент списка выше
@Composable
fun ListItem(item: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .size(170.dp)
            .padding(end = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MainColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Если время пустое, то мы выводим дату "дд.ММ"
            // Так мы можем использовать элемент и для списка дней и для списка часов
            val dateOrTime = if (item.time == "") item.date.slice(0..4) else item.time
            Text(
                text = dateOrTime,
                style = TextStyle(fontSize = 25.sp, fontFamily = fontFamily),
                color = Color.White
            )
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.iconName}@4x.png",
                contentDescription = "im5",
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = item.weatherName,
                style = TextStyle(fontSize = 25.sp, fontFamily = fontFamily),
                color = Color.White
            )

            // Добавление "°C" в конце температур
            val outputText = if (item.currentTemp.isEmpty()) "${
                item.maxTemp.toFloat().toInt()
            }°C / ${item.minTemp.toFloat().toInt()}°C" else "${
                item.currentTemp.toFloat().toInt()
            }°C"
            Text(
                text = outputText,
                style = TextStyle(fontSize = 25.sp, fontFamily = fontFamily),
                color = Color.White
            )
        }
    }
}

// Окно поиска города
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSearch(searchActive: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    // Текст, который вводит пользователь
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        containerColor = DarkBlue,
        onDismissRequest = { searchActive.value = false },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Enter city name:", color = Color.White, fontSize = 16.sp)
                TextField(colors = TextFieldDefaults.textFieldColors(
                    containerColor = DarkBlue,
                    textColor = Color.White
                ),
                    textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                    value = dialogText.value, onValueChange = { dialogText.value = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                searchActive.value = false
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { searchActive.value = false }) {
                Text(text = "Cancel")
            }
        }
    )
}
