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

// 	@Test
// 	public void testMain() {
// 		CinemaTicketProApplication.main(new String[] {});
// 	}

// 	@Test
// 	void testSayHi() {
// 		assertEquals("Hello, world!", controller.sayHi());
// 	}
}
