package com.discordbot.commands;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.discordbot.Service.AuthService;
import com.discordbot.Service.QuestionService;
import com.discordbot.models.Question;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QuizCommand extends ListenerAdapter{
    private final QuestionService questionService = new QuestionService();
    private final AuthService authService = new AuthService();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        MessageChannel channel = event.getChannel();

        if (user.isBot()) return;

        String[] message = event.getMessage().getContentRaw().split(" ");

        if (message[0].equalsIgnoreCase("!quiz")) {
            if (!authService.isAuthenticated(user)) {
                channel.sendMessage("Debes estar autenticado para usar este comando.").queue();
                return;
            }

            if (message.length < 2) {
                channel.sendMessage("Por favor, proporciona un tema para el quiz.\n1. tema1\n 2.tema2").queue();
                return;
            }

            String tema = message[1];
            List<Question> questions = questionService.getQuestions(tema);

            if (questions.isEmpty()) {
                channel.sendMessage("No se encontraron preguntas para el tema: " + tema).queue();
                return;
            }

            int score = 0;
            Random random = new Random();

            for (int i = 0; i < 5; i++) {
                Question question = questions.get(random.nextInt(questions.size()));
                channel.sendMessage("Pregunta: " + question.getQuestion()).queue();
                channel.sendMessage("Opciones:");
                List<String> options = question.getOption();
                for (int j = 0; j < options.size(); j++) {
                    channel.sendMessage((j + 1) + ". " + options.get(j)).queue();
                }

                // Esperar la respuesta del usuario
                waitForUserResponse(event, question, options);

                // Verificar si la respuesta del usuario es correcta
                if (event.getMessage() != null) {
                    String respuestaUsuario = event.getMessage().getContentRaw();
                    if (respuestaUsuario.equalsIgnoreCase(question.getCorrectAnswer())) {
                        score += 2;
                    }
                }

                }

            channel.sendMessage("Tu puntaje es: " + score + " / 10").queue();
        }
    }

    // Método para esperar la respuesta del usuario
    private void waitForUserResponse(MessageReceivedEvent event, Question question, List<String> options) {
        // Enviar un mensaje de espera
        event.getChannel().sendMessage("Por favor, selecciona una opción escribiendo su número correspondiente.").queue();

        // Esperar la respuesta del usuario por un tiempo limitado
        event.getChannel().sendMessage("Tienes 30 segundos para responder.").queue();
        event.getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                if (event.getAuthor().equals(event.getAuthor()) && event.getChannel().equals(event.getChannel())) {
                    try {
                        int choice = Integer.parseInt(event.getMessage().getContentRaw().trim());
                        if (choice >= 1 && choice <= options.size()) {
                            event.getChannel().sendMessage("Has seleccionado: " + options.get(choice - 1)).queue();
                            // Marcar el evento como completado para que no se escuche más
                            event.getJDA().removeEventListener(this);
                        } else {
                            event.getChannel().sendMessage("Por favor, selecciona un número válido.").queue();
                        }
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Por favor, introduce un número válido.").queue();
                    }
                }
            }
        });
        // Esperar 30 segundos para la respuesta del usuario
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Limpiar mensajes del usuario después de responder
        event.getChannel().getHistory().retrievePast(50).queue(messages -> {
            messages.removeIf(message -> message.getAuthor().equals(event.getAuthor()));
            event.getChannel().purgeMessages(messages);
        });
    }
}
