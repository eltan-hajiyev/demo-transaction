package com.example.demotransaction;

import java.lang.reflect.Proxy;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demotransaction.model.Book;
import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.BookRepository;
import com.example.demotransaction.repository.StudentRepository;

@SpringBootTest
class DemoProxyModelTests {
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private BookRepository bookRepository;

	private static Student studentInitial;

	@PostConstruct
	public void initSingleModel() {
		studentInitial = new Student();
		studentInitial.setName("Rustam");
		studentInitial.setStatus(false);

		Book book = new Book();
		book.setStudent(studentInitial);

		System.out.println("initial creating student: ");
		studentRepository.save(studentInitial);
		bookRepository.save(book);
	}

	@Test
	void test() throws InterruptedException {
		try {
			Student student = studentRepository.findById(1).get();

			System.out.println(Proxy.isProxyClass(student.getClass()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
