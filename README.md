# Mini Redis in Java

A lightweight, Redis-inspired in-memory key-value database built using Java.
This project demonstrates socket programming, multithreading, data structures,
and command processing.

## Features

- TCP client-server communication
- In-memory key-value storage
- String operations: SET, GET, DEL, INCR, DECR, APPEND
- Multiple-key operations: MSET, MGET
- Database commands: KEYS, DBSIZE, FLUSHDB, TYPE
- Key expiration: EXPIRE, TTL
- List operations: LPUSH, RPUSH, LRANGE, LPOP, RPOP
- Set operations: SADD, SREM, SMEMBERS
- Hash operations: HSET, HGET, HGETALL, HLEN
- Sorted-set operations: ZADD, ZSCORE, ZRANGE, ZCARD
- Multiple client connections using Java threads

## Technologies Used

- Java
- Java Socket Programming
- Multithreading
- Java Collections Framework
- TCP/IP Networking

## Project Structure

mini-redis-java/
├── src/
│   └── redis/
│       ├── RedisServer.java
│       ├── RedisDatabase.java
│       ├── CommandHandler.java
│       ├── ClientHandler.java
│       └── RedisClient.java
├── out/
└── README.md

## How to Run

### 1. Compile the project

Open Command Prompt in the project folder and run:

    javac -d out src\redis\*.java

### 2. Start the server

    java -cp out redis.RedisServer

### 3. Start the client

Open a second Command Prompt window and run:

    cd C:\Users\Bismita\OneDrive\Desktop\mini-redis-java
    java -cp out redis.RedisClient

## Example Commands

    PING
    SET name Bismita
    GET name
    INCR counter
    LPUSH fruits Apple
    LRANGE fruits 0 -1
    HSET student course CSE
    HGET student course
    ZADD scores 90 Bismita
    ZRANGE scores 0 -1

## Learning Outcomes

- Understanding client-server architecture
- Implementing TCP communication using Java sockets
- Managing multiple client connections
- Using Java collections to implement data structures
- Designing a command handler for database operations
- Implementing key expiration and common database commands

## Future Improvements

- Implement the official Redis RESP protocol
- Add persistence to save data to disk
- Improve command validation and error handling
- Add automated unit tests
- Improve compatibility with Redis clients

## Author

Bismita Das"# mini-redis-java" 
