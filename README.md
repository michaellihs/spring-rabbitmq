Spring RabbitMQ
===============

Spring sample project for using RabbitMQ with Spring. Based on [https://spring.io/guides/gs/messaging-rabbitmq/].



Prerequisites
-------------

* Install a local [RabbitMQ server](https://www.rabbitmq.com/install-standalone-mac.html)
  * RabbitMQ [config file](https://www.rabbitmq.com/configure.html) is expected in `/usr/local/etc/rabbitmq/rabbitmq.config` (go to Web UI to see the path on Windows machines)
* Start RabbitMQ with `rabbitmq-server`
  * RabbitMQ UI will be available at [http://localhost:15672/] afterwards



Building and Running the Application
------------------------------------

Either with

* Run `mvn package`
* Run `java -jar target/rabbitconsumer-0.0.1-SNAPSHOT.jar`

or simply

* `mvn spring-boot:run`



Using the Application
---------------------

The application provides two resources:

1. A message producer under [http://localhost:8080/producer]
1. A message consumer under [http://localhost:8080/consumer]

(TODO) The producer resource will allow you to post messages that will be added to the queue.

The consumer resource will show you a list of messages that a consumer got from the queue.


### Sending POST Requests to Producer

Here's the "settings" for sending a POST request with Postman:

![Sending Post Request with Postman](/doc/screen-1.png?raw=true "Sending Post Request with Postman")

And here is the corresponding CURL command:

    curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache" -d '{"messageType" : "12345", "messageBody" : "Here comes the content"}' "http://localhost:8080/producer"



Further Resources
=================

* [Spring Initializr](https://start.spring.io/)
* [Spring RabbitMQ Sample Project](https://spring.io/guides/gs/messaging-rabbitmq/)
