package com.sigma.cinematicketpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class CinemaTicketProApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaTicketProApplication.class, args);
	}

	@GetMapping
	public String sayHi() {
		return "Hello, world!";
	}
}
