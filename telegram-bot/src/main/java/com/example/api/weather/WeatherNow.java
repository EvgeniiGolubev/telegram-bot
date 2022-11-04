package com.example.api.weather;

import java.util.List;

public class WeatherNow {
    private List<Weather> weather;
    private Main main;

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }
}
