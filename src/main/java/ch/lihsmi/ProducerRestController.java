package ch.lihsmi;

import ch.lihsmi.rabbitproducer.Message;
import ch.lihsmi.rabbitproducer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer")
public class ProducerRestController {

    @Autowired
    Producer producer;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    String add(@RequestBody Message message) throws Exception {

        /**
         * TODO add message to queue
         * TODO what you get is a message object that is created from the
         * TODO POST request. You can use the content of the message object to
         * TODO put a new message into the queue.
         */

        producer.send(message);
        return "Received message: " + message.getMessageType() + "::" + message.getMessageBody();

    }

}
