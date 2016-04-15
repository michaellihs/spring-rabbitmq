package ch.lihsmi.rabbitconsumer;

public class Receiver {

    private MessageLogger messageLogger = MessageLogger.getInstance();

    private String consumerName;

    public Receiver(String consumerName) {
        this.consumerName = consumerName;
    }

    public void receiveMessage(String message) {
        messageLogger.log("[" + consumerName + "]  " + message);
    }

}