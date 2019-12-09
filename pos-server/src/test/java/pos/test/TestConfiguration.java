package pos.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot test configuration
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"pos.controller", "pos.service"})
public class TestConfiguration {}