package ch.lihsmi.rabbitconsumer;


public interface Receiver {

    public void receiveMessage(String message) throws Exception;

}