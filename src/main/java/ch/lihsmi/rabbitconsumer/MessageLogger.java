package ch.lihsmi.rabbitconsumer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageLogger {

    private static MessageLogger singletonInstance = new MessageLogger();

    private List<String> loggedMessages;

    public static MessageLogger getInstance() {
        return MessageLogger.singletonInstance;
    }

    private MessageLogger() {
        loggedMessages = new ArrayList<String>();
        loggedMessages.add("Started Logger");
    }

    public List<String> getLoggedMessages() {
        return loggedMessages;
    }

    public void log(String message) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        loggedMessages.add(dateFormat.format(date) + " --- " + message);
    }

}