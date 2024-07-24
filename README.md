## Weather App Compose

## Description
Weather App Compose is a weather forecast app developed using Jetpack Compose for Android. The app provides up-to-date weather information including temperature, humidity, wind speed and forecast for the coming days.

## Main Features
- **Current Weather**: Display current weather, temperature and other parameters.
- **Weather Forecast**: Forecast for several days ahead.
- **City Search**: Ability to search for different cities to display their weather forecast.
- **Intuitive Interface**: Utilizing Jetpack Compose to create a modern and user-friendly user interface.

## Requirements
- Android Studio Giraffe or higher
- Device or emulator with Android 5.0 (Lollipop) or higher

## Installation
1. **Clone the repository:**
    ```bash
    git clone https://github.com/Lem0nCat/weather_app_compose.git
    cd weather_app_compose
    ```

2. **Open in Android Studio:**
    - Open Android Studio.
    - Select “Open an existing Android Studio project”.
    - Locate and select the `weather_app_compose` folder.

3. **Customize the API key:**
    - Register and get the API key from [OpenWeatherMap](https://openweathermap.org/).
    - Locate the `app/src/main/java/en/lemoncat/weatherappcompose/MainActivity.kt` file and add your API key:
      ```kotlin
      const val API_KEY = “your_api_key”
      ```

4. **Build and run the application:**
    - Connect the device or start the emulator
