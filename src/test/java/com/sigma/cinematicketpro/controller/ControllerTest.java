package com.sigma.cinematicketpro.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ControllerTest {
    @Autowired
    private Controller controller;
    @Test
    void testSayHi() {
        assertEquals("Hello, world!", controller.sayHi());
    }
}