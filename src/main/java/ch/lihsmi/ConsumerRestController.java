package ch.lihsmi;

import ch.lihsmi.rabbitconsumer.MessageLogger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/consumer")
public class ConsumerRestController {

    private MessageLogger messageLogger = MessageLogger.getInstance();

    @RequestMapping(method = RequestMethod.GET)
    List<String> messages() {
        return messageLogger.getLoggedMessages();
    }

}
