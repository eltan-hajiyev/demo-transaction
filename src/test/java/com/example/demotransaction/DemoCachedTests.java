package com.example.demotransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.service.StudentService;

@SpringBootTest
class DemoCachedTests {
	@Autowired
	private StudentService studentService;

	@Test
	void test() {
		Student student1 = studentService.getCachedStudent(4);

		student1.setName("vdsdfgsdfggf");

		Student student2 = studentService.getCachedStudent(4);

		System.out.println(student1);
		System.out.println(student2);
	}

}
