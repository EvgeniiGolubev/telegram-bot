package com.example.handlers;

import com.example.api.currency.CurrencyApi;
import com.example.api.currency.CurrencyApiException;
import com.example.keyboard.ReplyKeyboardMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandler {
    private final ReplyKeyboardMaker replyKeyboardMarker;
    private final CurrencyApi currencyApi;

    @Autowired
    public CallbackQueryHandler(ReplyKeyboardMaker replyKeyboardMarker, CurrencyApi currencyApi) {
        this.replyKeyboardMarker = replyKeyboardMarker;
        this.currencyApi = currencyApi;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        String chatId = buttonQuery.getMessage().getChatId().toString();
        String value = buttonQuery.getData();

        String currency;
        switch (value) {
            case "USD":
                currency = "доллар";
                break;
            case "EUR":
                currency = "евро";
                break;
            case "RUB":
                currency = "рубль";
                break;
            default:
                currency = value;
        }

        String answer;
        try {
            answer = String.format("1 %s равен %s тенге", currency, currencyApi.getCurrencyValue(value));
        } catch (CurrencyApiException ex) {
            answer = ex.getMessage();
        }

        SendMessage sendMessage = new SendMessage(chatId, answer);
        sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
        return sendMessage;
    }
}
