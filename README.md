# Ecommerce microservices application in Kotlin

## Architecture Diagram

![Architecture Diagram](architecture-diagram.png)

## Microservices Architecture Design Patterns

In this project, I have implemented several design patterns commonly used in microservices architecture

| Design Pattern               | Tools                                                                   |
|------------------------------|-------------------------------------------------------------------------|
| Service Discovery            | [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix) |
| API Gateway                  | [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) |
| Circuit Breaker              | [Resilience4j](https://resilience4j.readme.io/docs/getting-startedy)    |
| Distributed Messaging System | [Apache Kafka](https://kafka.apache.org/)                               |

## Services and their ports

Below is the list of ports where each service is utilized in this project

| Services             | Port    |
|----------------------|---------|
| API Gateway          | 8080    |
| Discovery Server     | 8761    |
| Order Service        | 8081    |
| Product Service      | 8082    |
| Inventory Service    | Dynamic |
| Notification Service | 8083    |
| Zookeeper            | 2181    |
| Kafka Broker         | 9092    |
| Kaf Drop             | 9000    |