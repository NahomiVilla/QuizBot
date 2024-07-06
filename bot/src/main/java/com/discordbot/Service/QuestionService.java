package com.discordbot.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.discordbot.models.Question;

public class QuestionService {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/DiscordBot";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "S271994v";

    public List<Question> getQuestions(String tema) {
        List<Question> questions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT question, correct_answer FROM questions WHERE tema = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, tema);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String question = resultSet.getString("question");
                String correctAnswer = resultSet.getString("correct_answer");
                List<String> options = new ArrayList<>();
                options.add(resultSet.getString("option1"));
                options.add(resultSet.getString("option2"));
                options.add(resultSet.getString("option3"));
                questions.add(new Question(question, correctAnswer,options));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
