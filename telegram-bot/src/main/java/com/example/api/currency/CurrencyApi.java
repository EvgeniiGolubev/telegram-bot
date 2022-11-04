package com.example.api.currency;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CurrencyApi {
    public static String getCurrencyValue(String value){
        String URL = "https://www.google.com/finance/quote/value-KZT".replaceAll("value", value);
        Document document;
        try {
            document = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36")
                    .referrer("https://bot-for-test-on-java.herokuapp.com/")
                    .get();
        } catch (IOException e) {
            throw new HtmlPageParsingException("Ошибка в чтении валютных котировок.");
        }

        Elements list = document.getElementsByClass("YMlKec fxKbKc");
        String result = "0";
        for (Element element : list) {
            result = element.text();
        }

        return result;
    }
}