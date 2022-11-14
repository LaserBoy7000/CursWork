package com.labs.core.diagnostics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class Logger {
    public static final Logger INSTANCE = new Logger();
    private final String PATH = "logs/log%d-%d.txt";

    private Logger(){}

    public void log(String message, LogLevel level){
        message = LocalDateTime.now().toString() +":   "+ message;
        logFile(message);
        if(level == LogLevel.Fatal)
            sendNotification(message);
        
    }

    private void logFile(String string){
        LocalDateTime now = LocalDateTime.now();
        String actual = String.format(PATH, now.getMonthValue(), now.getDayOfMonth());
        File f = new File(actual);
        try
        {
            Files.createDirectories(f.getParentFile().toPath());
            BufferedWriter b = new BufferedWriter(new FileWriter(actual, true));
            b.write(string + '\n');
            b.close();
        } catch(Exception e){}
    } 

    public void sendNotification(String message){
        Email.INSTANCE.sendMessageToRoot(message);
    }
}
