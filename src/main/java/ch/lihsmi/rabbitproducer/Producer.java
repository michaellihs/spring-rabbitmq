package ch.lihsmi.rabbitproducer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    public final static String routingKey = "spring-boot";

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(Message message) throws Exception {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(routingKey, message.getMessageType() + "::" + message.getMessageBody());
    }

}
