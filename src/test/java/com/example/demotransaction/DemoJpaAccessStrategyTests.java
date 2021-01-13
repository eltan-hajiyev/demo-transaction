package com.example.demotransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demotransaction.model.Book;
import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.BookRepository;
import com.example.demotransaction.repository.StudentRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demotransaction.model.SchoolFieldBase;
import com.example.demotransaction.model.SchoolPropertyBase;
import com.example.demotransaction.service.SchoolFieldBaseRepository;
import com.example.demotransaction.service.SchoolPropertyBaseRepository;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
class DemoJpaAccessStrategyTests {

	@Autowired
	private SchoolFieldBaseRepository schoolFieldBaseRepository;

	@Autowired
	private SchoolPropertyBaseRepository schoolPropertyBaseRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Test
	void default_LazyLoading_will_not_work() throws InterruptedException {
		Student student = studentRepository.findById(1).get();

		System.err.println(student.getId() + ":" + student.getName());

		assertThrows(LazyInitializationException.class, () -> {
			Book book = student.getBooks().get(0);
		});
	}

	@Test
	@Transactional
	void LazyLoading_will_work_with_transactional() throws InterruptedException {
		Student student = studentRepository.findById(1).get();

		System.err.println(student);
	}

	@Test
	void FieldBase_JPA_annotation_uses_fields() throws InterruptedException {
		SchoolFieldBase school = schoolFieldBaseRepository.findById(1).get();

		System.err.println("::" + school);
		assertThat(school.getName()).doesNotEndWith("field");
		assertThat(school.getLocation()).doesNotEndWith("field");
	}

	@Test
	void tPropertyBase_JPA_annotation_uses_accessors() throws InterruptedException {
		SchoolPropertyBase school = schoolPropertyBaseRepository.findById(1).get();

		System.err.println("::" + school);
		assertThat(school.getName()).endsWith("property");
		assertThat(school.getLocation()).endsWith("property");
	}

}
