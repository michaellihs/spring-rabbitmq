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

* Run `mvn package`
* Run `java -jar target/rabbitconsumer-0.0.1-SNAPSHOT.jar`



Further Resources
=================

* [Spring Initializr](https://start.spring.io/)
* [Spring RabbitMQ Sample Project](https://spring.io/guides/gs/messaging-rabbitmq/)
