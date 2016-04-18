package ch.lihsmi.rabbitconsumer;



public class FaultyReceiver implements Receiver {

    private MessageLogger messageLogger = MessageLogger.getInstance();

    private String consumerName;

    public FaultyReceiver(String consumerName) {
        this.consumerName = consumerName;
    }

    public void receiveMessage(String message) throws Exception {
        messageLogger.log("[" + consumerName + "]  throws exception and does not handle " + message);
        throw new Exception("Receiver Exception");
    }

}
