package ch.lihsmi.rabbitconsumer;

public class Receiver {

    public void ReceiveMessage(String message) {
        System.out.println("Received Message: " + message);
    }

}