# API Gateway

API Gateway is a central entry point for routing, managing, monitoring, and securing APIs. 

I have focused on implementing the routing functionality, ensuring that every API call from the client is directed to relevant downstream services.

To achieve this, I have integrated the [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) library. 

## API - Health

This API provides the status of the API Gateway when it is started.

* Request
```
GET - http://localhost:8080/actuator/health
```
* Response
```
{
    "status": "UP"
}
```