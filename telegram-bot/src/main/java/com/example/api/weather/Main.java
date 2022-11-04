package com.example.api.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Main {
    private Integer temp;

    @JsonProperty("feels_like")
    private Integer feelsLike;

    public Integer getTemp() {
        return temp;
    }

    public Integer getFeelsLike() {
        return feelsLike;
    }
}
