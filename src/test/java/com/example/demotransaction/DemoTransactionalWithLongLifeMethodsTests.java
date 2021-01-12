package com.example.demotransaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import com.example.demotransaction.service.StudentFilterService;

/**
 * If use @Transactional
 * 
 * <pre>
 * {@code
 * }
 * </pre>
 * 
 * * If not use @Transactional
 * 
 * <pre>
 * {@code
 * }
 * </pre>
 * 
 */
@SpringBootTest
@TestPropertySource(properties = { "spring.datasource.hikari.maximum-pool-size=2" })
class DemoTransactionalWithLongLifeMethodsTests {
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	StudentFilterService studentFilterService;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	List<Integer> studentIdList = new ArrayList<>();

	@PostConstruct
	public void initSingleModel() {
		System.out.println("initial create of student list: ");
		for (int i = 0; i < 5; i++) {
			Student student = new Student();
			student.setName("Rustam: " + i);
			student.setStatus(false);
			studentRepository.save(student);

			studentIdList.add(student.getId());
		}
	}

	@Test
	/*-
	 */
	void testDefaultSelect() throws Exception {
		List<CompletableFuture<List<Student>>> futureList = new ArrayList<>();

		Arrays.asList(1, 2, 3, 4).forEach((i) -> {
			CompletableFuture<List<Student>> future = taskExecutor.submitListenable(() -> {
				return studentFilterService.filterWithoutTransactional(studentIdList, i);
			}).completable();

			futureList.add(future);
		});

		System.err.println("It will done immediatly. Will not wait end of future methods.");
		studentFilterService.easyTransactionalMethod();

		assertThat(futureList.stream().map(CompletableFuture::isDone)).contains(false);
	}

	@Test
	/*-
	 * 1. Don't do like this. Split you long life methods to short life @Transactional services.
	 */
	void testTransactionalSelect() throws Exception {
		List<CompletableFuture<List<Student>>> futureList = new ArrayList<>();

		Arrays.asList(1, 2, 3, 4).forEach((i) -> {
			CompletableFuture<List<Student>> future = taskExecutor.submitListenable(() -> {
				return studentFilterService.filterTransactional(studentIdList, i);
			}).completable();

			futureList.add(future);
		});

		System.err.println("EasyMethod list can be started! Bu it will not because transactional blocked sessions.");
		System.err.println("It will wait end of future methods to unblock session.");
		studentFilterService.easyTransactionalMethod();

		assertThat(futureList.stream().map(CompletableFuture::isDone)).contains(true);
	}

}
