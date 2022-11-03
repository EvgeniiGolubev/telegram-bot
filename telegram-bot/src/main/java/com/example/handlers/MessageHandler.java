package com.example.handlers;

import com.example.calculator.Calculator;
import com.example.calculator.exceptions.UnexpectedCharacter;
import com.example.calculator.exceptions.UnexpectedToken;
import com.example.keyboard.ReplyKeyboardMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler {
    private final ReplyKeyboardMaker replyKeyboardMarker;
    private final Calculator calculator;

    @Autowired
    public MessageHandler(ReplyKeyboardMaker replyKeyboardMarker, Calculator calculator) {
        this.replyKeyboardMarker = replyKeyboardMarker;
        this.calculator = calculator;
    }

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
        String inputText = message.getText();

        if (inputText == null) return new SendMessage(chatId, "Пусто \uD83E\uDD72");

        if (inputText.equals("/start")) {
            return getStartMessage(chatId);
        } else if (inputText.equals("Калькулятор")) {
            return new SendMessage(chatId,
                    "Внимание! Калькулятор считывает выражение со строки, поэтому будь аккуратнее с пробелами.\n" +
                            "Калькулятор поддерживает операции умножение, деление, сложение и вычитание.\n" +
                            "Напиши в чате Decide и своё выражение. Между каждым знаком ставь пробел.\n " +
                            "Например: Decide - 22 + 3 - 2 * ( - 2 * 5 + 2 ) * 4");
        } else if (inputText.startsWith("Decide")) {
            SendMessage sendMessage = getExpressionSolution(chatId, inputText);
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
            return sendMessage;
        }

        return new SendMessage(chatId, inputText);
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "Привет! Это тестовый telegram bot. На данный момент доступен только калькулятор");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMarker.getMainMenuKeyboard());
        return sendMessage;
    }

    private SendMessage getExpressionSolution(String chatId, String inputText) {

        String expression = inputText.replaceAll("Decide", "");

        if (expression.equals("")) return new SendMessage(chatId, "Вы ничего не ввели");

        int result;
        try {
            result = calculator.expressionSolution(expression);
        } catch (UnexpectedCharacter | UnexpectedToken ex) {
            return new SendMessage(chatId, ex.getMessage());
        }


        return new SendMessage(chatId, "Ваш результат: " + result + " Круто, правда? :)");
    }
}