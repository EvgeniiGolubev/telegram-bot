package com.example.api.currency;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CurrencyApi {
    public static String getCurrencyValue(String value){
        String url = "https://api.apilayer.com/fixer/convert?to=KZT&from=value&amount=1".replaceAll("value", value);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("apikey", "IDsOcDAEJsWpPhb2seSYFcCk3KOW0KSO");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Currency> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Currency.class);

        Currency currency = exchange.getBody();
        if (currency == null) {
            throw new CurrencyApiException("Ошибка в чтении валютных котировок.");
        }
        return currency.getResult().substring(0, 6);
    }
}