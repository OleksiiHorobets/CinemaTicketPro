package com.sigma.cinematicketpro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CinemaTicketProApplicationTests {
	@Autowired
	private CinemaTicketProApplication controller;

	@Test
	void contextLoads() {
		assertNotNull(controller);
	}

	@Test
	void sayHi() {
		assertEquals("Hello, world!", controller.sayHi());
	}
}
