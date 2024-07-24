package ru.lemoncat.weatherappcompose.data

data class WeatherModel(
    val city: String,
    var date: String,
    val time: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val weatherName: String,
    val description: String,
    val iconName: String
)
