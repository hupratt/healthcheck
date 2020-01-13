package com.aws.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
@ComponentScan
public class Application implements WebMvcConfigurer {
	@Autowired
	private DispatcherServlet servlet;

	@Bean
	public CommandLineRunner getCommandLineRunner(ApplicationContext context) {
		servlet.setThrowExceptionIfNoHandlerFound(true);
		return args -> {
		};
	}

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);

	}
}
