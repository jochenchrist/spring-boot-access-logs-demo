package de.jochenchrist.springboot.accesslogs;

import static net.logstash.logback.argument.StructuredArguments.v;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FooController {

  private static final Logger logger = LoggerFactory.getLogger(FooController.class);

  @GetMapping
  @ResponseBody
  public String getIndex() {
    logger.info("This is a normal log statement: {}", v("foo", "bar"));
    return "Foo";
  }
}
