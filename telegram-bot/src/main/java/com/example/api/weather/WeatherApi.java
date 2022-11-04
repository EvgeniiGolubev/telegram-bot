package com.example.api.weather;

import com.example.api.weather.exceptions.WeatherApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class WeatherApi {
    @Value("${telegram.weather-url-api}")
    private String URL;

    public String getWeatherNow(String city) {
        if (!isCity(city)) return "Ошибка в названии города";
        RestTemplate restTemplate = new RestTemplate();
        WeatherNow weatherNow = restTemplate.getForObject(URL.replaceAll("city", city), WeatherNow.class);

        if (weatherNow == null) {
            throw new WeatherApiException("Временно невозможно определить погоду.");
        }

        int temp = weatherNow.getMain().getTemp();
        int feelsLike = weatherNow.getMain().getFeelsLike();
        String description = weatherNow.getWeather().get(0).getDescription();

        return String.format("Погода в городе %s %d°C, ощущается как %d°C. На улице %s.", city, temp, feelsLike, description);
    }

    private boolean isCity(String city) {
        try {
            URL weatherUrl = new URL(URL.replaceAll("city", city));

            HttpURLConnection connection = (HttpURLConnection) weatherUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e)  {
            return false;
        }
    }
}
