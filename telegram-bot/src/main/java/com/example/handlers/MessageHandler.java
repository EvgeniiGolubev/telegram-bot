package com.example.handlers;

import com.example.api.calculator.Calculator;
import com.example.api.calculator.exceptions.UnexpectedCharacter;
import com.example.api.calculator.exceptions.UnexpectedToken;
import com.example.api.weather.exceptions.WeatherApiException;
import com.example.keyboard.InlineKeyboardMaker;
import com.example.keyboard.ReplyKeyboardMaker;
import com.example.api.weather.WeatherApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler {
    private final ReplyKeyboardMaker replyKeyboardMarker;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    private final Calculator calculator;
    private final WeatherApi weatherApi;

    @Autowired
    public MessageHandler(ReplyKeyboardMaker replyKeyboardMarker,
                          InlineKeyboardMaker inlineKeyboardMaker,
                          Calculator calculator,
                          WeatherApi weatherApi) {
        this.replyKeyboardMarker = replyKeyboardMarker;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        this.calculator = calculator;
        this.weatherApi = weatherApi;
    }

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
        String inputText = message.getText();

        if (inputText == null) {
            return getMessage(chatId, Messages.NOT_ALLOWED.getMessage());
        }

        if (inputText.equals("/start")) {
            return getMessage(chatId, Messages.GRATING_MESSAGES.getMessage());

        } else if (inputText.equals("Калькулятор")) {
            return new SendMessage(chatId, Messages.CALCULATOR_MESSAGES.getMessage());

        } else if (inputText.startsWith("Решить") || inputText.startsWith("решить")) {
            return getCalculatorMessage(chatId, inputText);

        } else if (inputText.equals("Погода")) {
            return new SendMessage(chatId, Messages.WEATHER_MESSAGES.getMessage());

        } else if (inputText.startsWith("Город") || inputText.startsWith("город")) {
            return getWeatherMessage(chatId, inputText);

        } else if (inputText.startsWith("Курс валют")){
            SendMessage sendMessage = new SendMessage(chatId, Messages.CURRENCY_MESSAGE.getMessage());
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons());
            return sendMessage;
        }

        return getMessage(chatId, "Мне нечего сказать \uD83D\uDE15");
    }

    private SendMessage getMessage(String chatId, String answer) {
        SendMessage sendMessage = new SendMessage(chatId, answer);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
        return sendMessage;
    }

    private SendMessage getCalculatorMessage(String chatId, String inputText) {
        SendMessage sendMessage = getCalculatorApi(chatId, inputText);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
        return sendMessage;
    }

    private SendMessage getWeatherMessage(String chatId, String inputText) {
        SendMessage sendMessage = getWeatherApi(chatId, inputText);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
        return sendMessage;
    }

    private SendMessage getWeatherApi(String chatId, String inputText) {
        String city = inputText.replaceAll("Город|город", "");

        if (city.equals("")) return new SendMessage(chatId, Messages.EMPTY_INPUT.getMessage());

        String answer;
        try {
            answer = weatherApi.getWeatherNow(city.trim());
        } catch (WeatherApiException ex) {
            answer = ex.getMessage();
        }

        return new SendMessage(chatId, answer);
    }

    private SendMessage getCalculatorApi(String chatId, String inputText) {
        String expression = inputText.replaceAll("Решить|решить", "");

        if (expression.equals("")) return new SendMessage(chatId, Messages.EMPTY_INPUT.getMessage());

        int result;
        try {
            result = calculator.expressionSolution(expression);
        } catch (UnexpectedCharacter | UnexpectedToken ex) {
            return new SendMessage(chatId, ex.getMessage());
        }

        return new SendMessage(chatId, "Ваш результат: " + result + " Круто, правда? :)");
    }
}