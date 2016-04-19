package ch.lihsmi.end2end;

import ch.lihsmi.Application;
import ch.lihsmi.rabbitconsumer.ConsumerBuilder;
import ch.lihsmi.rabbitconsumer.FaultyReceiver;
import ch.lihsmi.rabbitconsumer.Receiver;
import ch.lihsmi.rabbitproducer.Message;
import ch.lihsmi.rabbitproducer.Producer;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=Application.class)
public class UniqueMessageProcessingTest {

    @Autowired
    private ConsumerBuilder consumerBuilder;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Producer producer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void messagesAreProcessedBySingleConsumer() throws Exception {
        // given
        String routingKey = "key-1";
        CountDownLatch lock = new CountDownLatch(2); // "2" refers to the number of messages expected to be sent
        CountingReceiver countingReceiver = new CountingReceiver(lock);
        consumerBuilder.withReceiver(countingReceiver).build("testConsumer-1", routingKey, "test-1");

        // when
        producer.send(new Message(routingKey, "message 1"));
        producer.send(new Message(routingKey, "message 2"));
        lock.await(2000, TimeUnit.MILLISECONDS);

        // then
        assertEquals(2, countingReceiver.getMessageCount());
    }

    @Test
    public void messagesAreLoadBalancedAmongstConsumers() throws Exception {
        // given
        String routingKey = "key-2";
        CountDownLatch lock = new CountDownLatch(5);
        CountingReceiver consumer1 = new CountingReceiver(lock);
        CountingReceiver consumer2 = new CountingReceiver(lock);
        CountingReceiver consumer3 = new CountingReceiver(lock);
        consumerBuilder.withReceiver(consumer1).build("testConsumer-1", routingKey, "test-2");
        consumerBuilder.withReceiver(consumer2).build("testConsumer-2", routingKey, "test-2");
        consumerBuilder.withReceiver(consumer3).build("testConsumer-3", routingKey, "test-2");

        // when
        for (int i = 1; i <= 5; i++) { producer.send(new Message(routingKey, "message " + Integer.toString(i))); }
        lock.await(2000, TimeUnit.MILLISECONDS);

        // then
        assertEquals(5, consumer1.getMessageCount() + consumer2.getMessageCount() + consumer3.getMessageCount());
        assertTrue(consumer1.getMessageCount() >= 1);
        assertTrue(consumer2.getMessageCount() >= 1);
        assertTrue(consumer3.getMessageCount() >= 1);
    }

    @Test
    public void messagesAreReassignedToAnotherConsumerIfOneConsumerFails() throws Exception {
        // given
        String routingKey = "key-3";
        CountDownLatch lock = new CountDownLatch(2);
        CountingReceiver consumer1 = new CountingReceiver(lock);
        FaultyReceiver consumer2 = new FaultyReceiver("faulty consumer");
        consumerBuilder.withReceiver(consumer1).build("testConsumer-1", routingKey, "test-3");
        consumerBuilder.withReceiver(consumer2).build("testConsumer-2", routingKey, "test-3");

        // when
        producer.send(new Message(routingKey, "message 1"));
        producer.send(new Message(routingKey, "message 2"));
        lock.await(2000, TimeUnit.MILLISECONDS);

        // then
        assertEquals(2, consumer1.getMessageCount());
    }

    @Test
    public void messagesAreNotRoutedToBusyConsumers() throws Exception {
        // given
        String routingKey = "key-4";
        CountDownLatch lock = new CountDownLatch(3);
        BusyCountingReceiver busyCountingReceiver = new BusyCountingReceiver(lock, 1000);  // consumer needs 1 sec to handle message
        CountingReceiver countingReceiver = new CountingReceiver(lock);
        consumerBuilder.withReceiver(countingReceiver).build("testConsumer-1", routingKey, "test-4");
        consumerBuilder.withReceiver(busyCountingReceiver).build("testConsumer-2", routingKey, "test-4");

        // when
        for (int i = 1; i <= 3; i++) { producer.send(new Message(routingKey, "message " + Integer.toString(i))); }
        lock.await(20000, TimeUnit.MILLISECONDS);

        // then
        assertEquals(2, countingReceiver.getMessageCount());
        assertEquals(1, busyCountingReceiver.getMessageCount());
    }

    @Test
    public void messagesAreFannedOutToMultipleConsumerPools() throws Exception {
        // given
        String routingKey = "key-5";
        CountDownLatch lock = new CountDownLatch(8);
        CountingReceiver[] consumerPool1 = {new CountingReceiver(lock), new CountingReceiver(lock)};
        CountingReceiver[] consumerPool2 = {new CountingReceiver(lock), new CountingReceiver(lock)};
        for (CountingReceiver receiver : consumerPool1) { consumerBuilder.withReceiver(receiver).build("test-consumer", routingKey, "test-5-1"); }
        for (CountingReceiver receiver : consumerPool2) { consumerBuilder.withReceiver(receiver).build("test-consumer", routingKey, "test-5-2"); }

        // when
        List<String> expectedMessagesInPools = new ArrayList<>(Arrays.asList(new String[] {"1", "2", "3", "4"}));
        for (String message : expectedMessagesInPools) { producer.send(new Message(routingKey, message)); }
        lock.await(2000, TimeUnit.MILLISECONDS);

        // then
        assertEquals(4, consumerPool1[0].getMessageCount() + consumerPool1[1].getMessageCount());
        assertEquals(4, consumerPool2[0].getMessageCount() + consumerPool2[1].getMessageCount());

        List<String> messagesConsumedByPool1 = consumerPool1[0].getMessages(); messagesConsumedByPool1.addAll(consumerPool1[1].getMessages());
        List<String> messagesConsumedByPool2 = consumerPool2[0].getMessages(); messagesConsumedByPool2.addAll(consumerPool2[1].getMessages());
        assertThat(expectedMessagesInPools, IsIterableContainingInAnyOrder.containsInAnyOrder(messagesConsumedByPool1.toArray()));
        assertThat(expectedMessagesInPools, IsIterableContainingInAnyOrder.containsInAnyOrder(messagesConsumedByPool2.toArray()));
    }

    /*

    this test illustrates how to set up "everything by hand"

    @Test
    public void manualTest() throws Exception {

        // for the async waiting with the CountDownLatch, see http://stackoverflow.com/questions/631598/how-to-use-junit-to-test-asynchronous-processes
        // the "1" here refers to the number of messages we are expecting to be sent.
        CountDownLatch lock = new CountDownLatch(1);

        String queueName = "test-queue";
        String routingKey = "test-routing-key";
        CountingReceiver receiver = new CountingReceiver(lock);

        // set up the queue, exchange, binding on the broker
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange(GlobalConfig.DISTRIBUTION_EXCHANGE);
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));

        // set up the listener and container
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);

        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage");
        container.setMessageListener(adapter);
        container.setQueueNames(queueName);
        container.start();

        rabbitTemplate.convertAndSend(GlobalConfig.DISTRIBUTION_EXCHANGE, routingKey, "message body");

        // we wait max 2000 millisecs for the message to be processed
        lock.await(2000, TimeUnit.MILLISECONDS);

        assertEquals(1, receiver.getMessageCount());
    }
    */



    private class CountingReceiver implements Receiver {

        private final CountDownLatch lock;

        private int messageCount = 0;

        private ArrayList<String> consumedMessages;

        public CountingReceiver(CountDownLatch lock) {
            this.lock = lock;
            this.consumedMessages = new ArrayList<>();
        }

        public int getMessageCount() {
            return messageCount;
        }

        public ArrayList<String> getMessages() {
            return consumedMessages;
        }

        @Override
        public void receiveMessage(String message) throws Exception {
            System.out.println("Processing Message...");
            this.messageCount++;
            this.consumedMessages.add(message);

            // once the message is processed, we count down the lock
            lock.countDown();
        }

    }

    private class BusyCountingReceiver extends CountingReceiver {

        private int runtime;

        public BusyCountingReceiver(CountDownLatch lock, int runtime) {
            super(lock);
            this.runtime = runtime;
        }

        @Override
        public void receiveMessage(String message) throws Exception {
            System.out.println("Before sleep...");
            Thread.sleep(runtime);
            System.out.println("...after sleep");
            super.receiveMessage(message);
        }

    }

}
