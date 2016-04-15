package ch.lihsmi.rabbitproducer;

import ch.lihsmi.GlobalConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    public final static String routingKey = "device-sample";

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(Message message) throws Exception {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(GlobalConfig.distributionExchange, message.getMessageType(), message.getMessageBody());
    }

}
