# Baja

Baja is a collection of low level Java building blocks for interacting with a 
[Redis](https://github.com/antirez/redis) server. By itself, it can't yet be 
considered a Redis client.


## Usage


```xml
<dependency>
    <groupId>org.tshlabs.baja</groupId>
    <artifactId>baja</artifactId>
    <version>x.y.z</version>
</dependency>
```


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
    
// res = "bar"

```
