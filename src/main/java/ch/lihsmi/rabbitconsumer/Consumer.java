package ch.lihsmi.rabbitconsumer;

import ch.lihsmi.GlobalConfig;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

public class Consumer {

    private final ConnectionFactory connectionFactory;

    /**
     * The routing key is the message type that the consumer wants to read from the queue.
     */
    private String routingKey;

    /**
     * The queue name is the name of the queue that is shared between a pool of
     * consumers for load balancing the messages within the same service.
     */
    private String queueName;

    private String name;

    public Consumer(String consumerName, String routingKey, String queueName, ConnectionFactory connectionFactory) {
        this.name = consumerName;
        this.routingKey = routingKey;
        this.queueName = queueName;
        this.connectionFactory = connectionFactory;

        initContainer();
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getName() {
        return name;
    }

    private void initContainer() {
        // set up the queue, exchange, binding on the broker
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange(GlobalConfig.distributionExchange);
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));

        // set up the listener and container
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);

        MessageListenerAdapter adapter = new MessageListenerAdapter(new Receiver(name), "receiveMessage");
        container.setMessageListener(adapter);
        container.setQueueNames(queueName);
        container.start();
    }

}
