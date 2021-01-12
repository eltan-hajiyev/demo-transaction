package com.example.demotransaction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demotransaction.model.SchoolFieldBase;
import com.example.demotransaction.model.SchoolPropertyBase;
import com.example.demotransaction.service.SchoolFieldBaseRepository;
import com.example.demotransaction.service.SchoolPropertyBaseRepository;

@SpringBootTest
class DemoJpaAccessStrategyTests {

	@Autowired
	private SchoolFieldBaseRepository schoolFieldBaseRepository;

	@Autowired
	private SchoolPropertyBaseRepository schoolPropertyBaseRepository;

	@Test
	void testFieldBaseJpaAnnotation() throws InterruptedException {
		SchoolFieldBase student = schoolFieldBaseRepository.findById(1).get();

		System.err.println("::" + student);
		assertThat(student.getName()).doesNotEndWith("field");
		assertThat(student.getLocation()).doesNotEndWith("field");
	}

	@Test
	void testPropertyBaseJpaAnnotation() throws InterruptedException {
		SchoolPropertyBase student = schoolPropertyBaseRepository.findById(1).get();

		System.err.println("::" + student);
		assertThat(student.getName()).endsWith("property");
		assertThat(student.getLocation()).endsWith("property");
	}

}
