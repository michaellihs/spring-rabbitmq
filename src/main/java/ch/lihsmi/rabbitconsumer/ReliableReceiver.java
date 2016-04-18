package ch.lihsmi.rabbitconsumer;

public class ReliableReceiver implements Receiver {

    private MessageLogger messageLogger = MessageLogger.getInstance();

    private String consumerName;

    public ReliableReceiver(String consumerName) {
        this.consumerName = consumerName;
    }

    @Override
    public void receiveMessage(String message) throws Exception {
        messageLogger.log("[" + consumerName + "]  " + message);
    }

}
