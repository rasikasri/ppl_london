package com.london;

import com.london.ws.controller.PeopleController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LondonPeopleApplicationTests {

	@Autowired
	private PeopleController controller;

	@Test
	void contextLoads() {
		assertNotNull(controller, "Context loading error");
	}

}
