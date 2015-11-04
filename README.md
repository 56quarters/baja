# Baja

Baja is a collection of low level Java building blocks for interacting with a 
[Redis](https://github.com/antirez/redis) server. By itself, it can't yet be 
considered a Redis client.


## Usage


### Includes

```xml
<dependency>
    <groupId>org.tshlabs.baja</groupId>
    <artifactId>baja</artifactId>
    <version>x.y.z</version>
</dependency>
```

### Single Commands

Executing single Redis commands at a time.

```java
Socket socket = new Socket(redisHost, redisPort);
InputStream in = socket.getInputStream();
OutputStream out = socket.getOutputStream();

RedisConnection connection = new RedisConnection(
    in, out, new RespEncoder(), new RespParser());

RedisCommand.cmd("SELECT")
    .arg(1)
    .query(connection)
    .discard();
                
RedisCommand.cmd("SET")
    .arg("foo")
    .arg("bar")
    .query(connection)
    .discard();
    
String res = RedisCommand.cmd("GET")
    .arg("foo")
    .query(connection)
    .asString();
    
System.out.println(res); // "bar"

```

### Multiple Commands

Executing multiple Redis commands in the context of a transaction.

```java
Socket socket = new Socket(redisHost, redisPort);
InputStream in = socket.getInputStream();
OutputStream out = socket.getOutputStream();

RedisConnection connection = new RedisConnection(
    in, out, new RespEncoder(), new RespParser());

Transaction transaction = connection.transaction();

RedisCommand.cmd("SELECT")
    .arg(1)
    .queue(transaction)
    .discard();

RedisCommand.cmd("SET")
    .arg("k1")
    .arg("v1")
    .queue(transaction)
    .discard();

RedisCommand.cmd("SET")
    .arg("k2")
    .arg("v2")
    .queue(transaction)
    .discard();

Result<String> res1 = RedisCommand.cmd("GET")
    .arg("k1")
    .queue(connection)
    .asString();

Result<String> res2 = RedisCommand.cmd("GET")
    .arg("k2")
    .queue(connection)
    .asString();

transaction.execute();

System.out.println(res1.get()); // "v1"
System.out.println(res2.get()); // "v2"

```
