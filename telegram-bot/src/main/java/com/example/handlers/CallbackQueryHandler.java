package com.example.handlers;

import com.example.keyboard.ReplyKeyboardMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandler {
    private final ReplyKeyboardMaker replyKeyboardMarker;

    @Autowired
    public CallbackQueryHandler(ReplyKeyboardMaker replyKeyboardMarker) {
        this.replyKeyboardMarker = replyKeyboardMarker;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        String chatId = buttonQuery.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage(chatId,  buttonQuery.getData() + " from method processCallbackQuery");
//        sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
        return sendMessage;
    }
}
