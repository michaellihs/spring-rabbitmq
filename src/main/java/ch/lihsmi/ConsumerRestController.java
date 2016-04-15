package ch.lihsmi;

import ch.lihsmi.rabbitconsumer.Consumer;
import ch.lihsmi.rabbitconsumer.MessageLogger;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consumer")
public class ConsumerRestController {

    @Autowired
    ConnectionFactory connectionFactory;

    private MessageLogger messageLogger = MessageLogger.getInstance();

    private Map<String, Consumer> consumerPool = new HashMap<String, Consumer>();

    @RequestMapping(path = "register/{consumerName}/{queueName}/{routingKey}", method = RequestMethod.POST)
    String register(@PathVariable String consumerName, @PathVariable String queueName, @PathVariable String routingKey) {
        consumerPool.put(consumerName, new Consumer(consumerName, routingKey, queueName, connectionFactory));
        return "Registered new consumer: " + consumerName;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<String> messages() {
        return messageLogger.getLoggedMessages();
    }

}
