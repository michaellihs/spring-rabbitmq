package ch.lihsmi.rabbitconsumer;

import java.util.ArrayList;
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

}