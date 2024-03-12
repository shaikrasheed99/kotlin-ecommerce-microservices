# Ecommerce microservices application in Kotlin

## Architecture Diagram

![Architecture Diagram](images/architecture-diagram.png)

## Microservices Architecture Design Patterns

In this project, I have implemented several design patterns commonly used in microservices architecture

| Design Pattern               | Tools                                                                   |
|------------------------------|-------------------------------------------------------------------------|
| Service Discovery            | [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix) |
| API Gateway                  | [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) |
| Circuit Breaker              | [Resilience4j](https://resilience4j.readme.io/docs)                     |
| Distributed Messaging System | [Apache Kafka](https://kafka.apache.org/)                               |

## How to set up the project?

1. Clone the repository.
    - Run the below command to download the code from Github repository to your local machine.
        ```bash
        git clone https://github.com/shaikrasheed99/kotlin-ecommerce-microservices.git
        ```
    - Run the below command to move to the application directory.
        ```bash
        cd kotlin-ecommerce-microservices
        ```

2. Make sure your machine has docker installed in it.
    - Make sure your terminal is in `kotlin-ecommerce-microservices` directory.
    - Run the below command to start the Zookeeper, Kafka broker & Kafdrop containers.
       ```bash
       docker compose up -d
       ```

3. Build & Start each service.
    - Before starting any service, make sure your postgresql database in your local is started.
    - For each service, open a new terminal and execute the below gradle commands one by one.
    - Follow the below sequence of commands in new terminal for every service. 
       ```bash
      cd <each service>
      ./gradlew clean build
      ./gradlew bootrun
       ```

    | Services             | Health API to validate                             | Expected success response                  |
    |----------------------|----------------------------------------------------|--------------------------------------------|
    | Order Service        | Postman - `http://localhost:8081/actuator/health`  | Status - 200 & Body - `{ "status": "UP" }` |
    | Product Service      | Postman - `http://localhost:8082/actuator/health`  | Status - 200 & Body - `{ "status": "UP" }` |
    | Inventory Service    | Postman - `http://localhost:53391/actuator/health` | Status - 200 & Body - `{ "status": "UP" }` |
    | Notification Service | Postman - `http://localhost:8083/actuator/health`  | Status - 200 & Body - `{ "status": "UP" }` |
    | API Gateway          | Postman - `http://localhost:8080/actuator/health`  | Status - 200 & Body - `{ "status": "UP" }` |
    | Discovery Server     | Browser - `http://localhost:8761/`                 | You must see `Spring Eureka` default UI    |
    | Kaf Drop             | Browser - `http://localhost:9000/`                 | You must see `Kafdrap` default UI          |

    