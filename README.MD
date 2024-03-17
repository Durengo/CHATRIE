# CHATRIE

Simple chatting app.

* The backend is made with [Spring Boot](https://spring.io/), [Cassandra](https://cassandra.apache.org/), [RabbitMQ](https://www.rabbitmq.com/)
* The frontend is made with [Node.js](https://nodejs.org/), [Svelte](https://svelte.dev/) + [Typescript](https://svelte.dev/docs/typescript)

## Requirements

1. [Node.js](https://nodejs.org/)
2. [Python>=3.0](https://www.python.org/)
3. [Docker](https://www.docker.com/)

## Setup

1. Setup Python environment
    * $python -m venv venv
    * $./venv/Scripts/activate
    * pip install -r ./requirements.txt
2. Setup the Docker environment with the provided scripts (Cassandra and RabbitMQ containers)
    * Make sure that Docker Desktop is running
    * $python ./Tools/setup.py
    * 1 To setup and run the Cassandra container (it will take a minute or two for Docker to set the server up so other commands might throw errors during this time)
    * 2 To setup the keyspace
    * 3 To stop the container
    * Deleting the container is currently done manually
    * 4 To setup and run the RabbitMQ container
3. Setup the backend
    * Using and IDE like IntelliJ open the "Server/CHATRIE" project, wait for setup, and run the main function
4. Setup the frontend
    * $npm install
5. Launch the web app
    * $npm run dev -- --open

## Packages used

### Backend

Spring Boot Packages:

1. [spring-boot-starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)
2. [spring-boot-starter-websocket](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-websocket)
3. [spring-data-envers](https://mvnrepository.com/artifact/org.springframework.data/spring-data-envers)
4. [spring-boot-starter-security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security) - used for user authentication and authorization (utilizes JWT)

Security:

1. [JJWT-API](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api)
2. [JJWT-IMPL](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl)
3. [JJWT-JACKSON](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson)

Database:

1. [Cassandra](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-cassandra)

Message Broker:

1. [RabbitMQ](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-amqp)

Utility:

1. [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok) - boilerplate code reduction

Sockets:

1. [SocketIO/NettySocketIO](https://mvnrepository.com/artifact/com.corundumstudio.socketio/netty-socketio) - could not use built-in Spring Boot Websocket functionality since SockJS or WS protocol did not properly connect through SvelteKit, so an alternative had to be used

Testing:

1. [junit](https://mvnrepository.com/artifact/junit/junit)

Logging:

1. [slf4j](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)
2. [logback-classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic)

### Frontend

These packages are automatically installed by following the [setup](#setup) section.

1. [Python Docker Package](https://pypi.org/project/docker/)
2. [NPM Cassandra Package](https://www.npmjs.com/package/cassandra/)
3. [socket.io-client](https://www.npmjs.com/package/socket.io-client)

## More

Additional NPM build info can be found [here](/Client/NPM.MD)
