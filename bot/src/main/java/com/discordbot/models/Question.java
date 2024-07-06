package com.discordbot.models;

import java.util.List;

public class Question {
    private final String question;
    private final String correctAnswer;
    private final List<String> option;

    public Question(String question, String correctAnswer,List<String> option) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.option=option;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public List<String> getOption(){
        return option;
    }
}
