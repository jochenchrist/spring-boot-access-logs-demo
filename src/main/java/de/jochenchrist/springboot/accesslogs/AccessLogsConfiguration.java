package de.jochenchrist.springboot.accesslogs;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
