# Position Book System

This is a position book system that allows users to manage their positions in various assets. 
The system provides functionalities to add BUY/SELL/CANCEL trade events and retrieve a summary of the position book; data is kept in memory.

## Features

### REST API Endpoints
- **POST /api/v1/createEvent**: Add a list of trade events (BUY/SELL/CANCEL).
- **POST /api/v1/createSingleEvent**: Add a single trade event
- **GET /api/v1/getPositionSummary**: Retrieve the current position book summary.
- **GET /api/v1/getPositionSummary/{accountId}**: Retrieve the position summary for a specific account and security.

### Data structures
The main data structure that was used in the project is a HashMap. 
Three HashMaps were used to store the position book, the totalQuantity of trades per account per security, and an idEventMap to map event IDs to their respective events for quick access to the actionType.

### Exceptions
- **InvalidTradeEventException**: Thrown when an invalid trade action type is used or when a CANCEL event is executed for the wrong account/security. [HTTP status code: 500 internal server error]
- **TradeEventIDNotFoundException**: Thrown for cancel trade events when the given ID is not found. [HTTP status code: 404 not found]
- **DuplicatedEventIDBadRequestException**: Thrown when the given trade event ID already exists in the position book for BUY/SELL events. [HTTP status code: 400 bad request]

### CORS configuration
Cross-Origin Resource Sharing (CORS) is configured to allow GET and POST requests from http://localhost:3000

## Tech and Getting Started
This project is built using Spring Boot and Gradle. To get started, you can clone the repository and run the application using the following commands:

```
./gradlew build
./gradlew bootRun
```

To check the status of the application (healthcheck), navigate to the below URL:
`http://localhost:8080/actuator/health`

To run tests, run:
`./gradlew test`