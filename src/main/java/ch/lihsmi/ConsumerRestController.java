package ch.lihsmi;

import ch.lihsmi.rabbitconsumer.Consumer;
import ch.lihsmi.rabbitconsumer.ConsumerBuilder;
import ch.lihsmi.rabbitconsumer.MessageLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consumer")
public class ConsumerRestController {

    @Autowired
    private ConsumerBuilder consumerBuilder;

    private MessageLogger messageLogger = MessageLogger.getInstance();

    private Map<String, Consumer> consumerPool = new HashMap<String, Consumer>();

    @RequestMapping(path = "register/{consumerName}/{queueName}/{routingKey}", method = RequestMethod.POST)
    String register(
            @PathVariable String consumerName,
            @PathVariable String queueName,
            @PathVariable String routingKey,
            @RequestParam(value = "faultyConsumer", required = false, defaultValue = "false") boolean faultyConsumer,
            @RequestParam(value = "runtime", required = false, defaultValue = "0") int runtime
    ) {
        consumerPool.put(consumerName, consumerBuilder.withFaultyReceiver(faultyConsumer).withRuntime(runtime).build(consumerName, routingKey, queueName));
        messageLogger.log("Registered new consumer: " + consumerName);
        return "Registered new consumer: " + consumerName;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<String> messages() {
        return messageLogger.getLoggedMessages();
    }

}
