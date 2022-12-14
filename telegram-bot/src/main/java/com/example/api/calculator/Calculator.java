package com.example.api.calculator;

import com.example.api.calculator.exceptions.UnexpectedCharacter;
import com.example.api.calculator.exceptions.UnexpectedToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Calculator {
    private final String unexpectedCharacter = "Неожиданный символ: '%s'";
    private final String unexpectedToken = "Неожиданный знак: '%s' в позиции: %s";

    public double expressionSolution(String expression) {
        List<Lexeme> lexemes = lexAnalyze(crutchForMinusSign(expression));
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        return expr(lexemeBuffer);
    }

    // Normally could not solve the problem of negative numbers
    private String crutchForMinusSign(String expression) {
        List<String> list = new ArrayList<>();
        for (String s : expression.trim().split(" ")) {
            if (!s.equals(" ")) list.add(s);
        }

        for (int i = 0; i < list.size(); i++) {
            if (i == 0 && list.get(i).equals("-")) {
                list.add(i, "(0");
                list.add(i + 3, ")");
            } else if (list.get(i).equals("-") && list.get(i - 1).equals("(")) {
                list.add(i, "(0");
                list.add(i + 3, ")");
            }
        }

        return String.join("", list);
    }


    private double expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.getType() == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    private double plusminus(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.getType()) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;
                default:
                    throw new UnexpectedToken(String.format(unexpectedToken, lexeme.getValue(), lexemes.getPos()));
            }
        }
    }

    private double multdiv(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.getType()) {
                case OP_MUL:
                    value *= factor(lexemes);
                    break;
                case OP_DIV:
                    value /= factor(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new UnexpectedToken(String.format(unexpectedToken, lexeme.getValue(), lexemes.getPos()));
            }
        }
    }

    private double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.getType()) {
            case NUMBER:
                return Integer.parseInt(lexeme.getValue());
            case LEFT_BRACKET:
                double value = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.getType() != LexemeType.RIGHT_BRACKET) {
                    throw new UnexpectedToken(String.format(unexpectedToken, lexeme.getValue(), lexemes.getPos()));
                }
                return value;
            default:
                throw new UnexpectedToken(String.format(unexpectedToken, lexeme.getValue(), lexemes.getPos()));
        }
    }

    private List<Lexeme> lexAnalyze(String expText) {
        List<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else {
                        if (c != ' ') {
                            throw new UnexpectedCharacter(String.format(unexpectedCharacter, c));
                        }
                        pos++;
                    }
            }
        }

        lexemes.add(new Lexeme(LexemeType.EOF, ""));

        return lexemes;
    }
}
