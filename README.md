# Spring Boot Tomcat Access Logs as JSON to stdout

How to enable Tomcat's access logs with Spring Boot and write them encoded as JSON to stdout.

With Spring Boot, access logs could be enabled with `server.tomcat.accesslog.enabled`, but this writes the access logs to a temporary file.

In a containerized or cloud-native environments, logs [are collected from stdout and forwarded to a log collector, such as Elasticsearch](https://www.innoq.com/de/blog/logging-mit-docker-und-elasticsearch/).

This demo shows how to configure Spring Boot 2 to write access logs to console and encode them as JSON.

## Example

```
{"@timestamp":"2020-05-14T22:12:33.780+02:00","@version":"1","message":"This is a normal log statement: bar","logger_name":"de.jochenchrist.springboot.accesslogs.FooController","thread_name":"http-nio-8080-exec-1","level":"INFO","level_value":20000,"foo":"bar"}
{"@timestamp":"2020-05-14T22:12:33.836+02:00","@version":"1","@type":"access","client-host":"0:0:0:0:0:0:0:1","remote-user":"-","request-message":"GET","request-url":"GET / HTTP/1.1","request-uri":"/","status-code":200,"bytes-sent":3,"elapsed-time":164,"message":"GET / HTTP/1.1 200"}
```

## Dependencies

Add to your _pom.xml_:

```xml
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-access</artifactId>
    </dependency>
    <dependency>
      <groupId>net.logstash.logback</groupId>
      <artifactId>logstash-logback-encoder</artifactId>
      <version>6.2</version>
    </dependency>
```

## WebServerFactoryCustomizer

Add a WebServerFactoryCustomizer, that adds the [LogbackValve](http://logback.qos.ch/access.html) to the WebServerFactory.
The default filename for the logback configuration is `logback-access.xml`, which can be changed here.

```java
@Configuration
public class AccessLogsConfiguration {

  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> accessLogsCustomizer() {
    return factory -> {
      var logbackValve = new LogbackValve();
      logbackValve.setFilename("logback-access.xml");
      logbackValve.setAsyncSupported(true);
      factory.addContextValves(logbackValve);
    };
  }
}
```

## logback-access.xml

Put the logback configuration file in _src/main/resources_ (probably next to your existing logback.xml for the Spring application).
Configure it to use the [AccessEventCompositeJsonEncoder](https://github.com/logstash/logstash-logback-encoder/blob/master/src/main/java/net/logstash/logback/encoder/AccessEventCompositeJsonEncoder.java) (provided by [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder)) with your desired JSON structure:

```xml
<configuration debug="false">
  <appender name="accessJsonConsoleAppender" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.AccessEventCompositeJsonEncoder">
      <providers>
        <timestamp />
        <pattern>
          <pattern>
            {
            "@version" : "1",
            "@type" : "access",
            "client-host" : "%clientHost",
            "remote-user" : "%user",
            "request-message" : "%requestMethod",
            "request-url" : "%requestURL",
            "request-uri" : "%requestURI",
            "status-code" : "#asLong{%statusCode}" ,
            "bytes-sent" : "#asLong{%bytesSent}",
            "elapsed-time" : "#asLong{%elapsedTime}",
            "message" : "%requestURL %statusCode"
            }
          </pattern>
        </pattern>
      </providers>
    </encoder>
  </appender>
  ​
  <appender-ref ref="accessJsonConsoleAppender" />
  ​
</configuration>
```

## Credits

Thanks to my colleagues [Martin Eigenbrodt](https://www.innoq.com/en/staff/martin-eigenbrodt/) for the initial concept and [Timo Loist](https://www.innoq.com/en/staff/timo-loist/) for pointing me to this solution.
