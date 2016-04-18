package ch.lihsmi.rabbitconsumer;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerBuilder {

    @Autowired
    ConnectionFactory connectionFactory;

    private boolean withFaultyReceiver = false;

    public Consumer build(String consumerName, String routingKey, String queueName) {
        Receiver receiver;
        if (withFaultyReceiver) {
            receiver = new FaultyReceiver(consumerName);
        } else {
            receiver = new ReliableReceiver(consumerName);
        }
        return new Consumer(consumerName, routingKey, queueName, connectionFactory, receiver);
    }

    public ConsumerBuilder withFaultyReceiver(boolean withFaultyReceiver) {
        if (withFaultyReceiver) this.withFaultyReceiver = true;
        return this;
    }
}
