package com.discordbot;
import com.discordbot.commands.QuizCommand;
import com.discordbot.config.Config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class DiscordBot {
    public static void main(String[] args) throws Exception {
        JDABuilder builder = JDABuilder.createDefault(Config.BOT_TOKEN);
        builder.setActivity(Activity.playing("Starting up..."));
        builder.addEventListeners(new QuizCommand());
        builder.build();
    }
    
}