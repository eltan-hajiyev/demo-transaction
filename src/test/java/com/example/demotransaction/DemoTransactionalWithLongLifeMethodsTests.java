package com.example.demotransaction;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import com.example.demotransaction.service.StudentFilterService;
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

import static org.assertj.core.api.Assertions.assertThat;

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
	public void init_list() {
		System.out.println("initial create of student list: ");
		studentRepository.findAll().iterator().forEachRemaining(st -> {
			studentIdList.add(st.getId());
		});
	}

	@Test
	/*-
	 */
	void default_transactional_long_life_methods() throws Exception {
		List<CompletableFuture<List<Student>>> futureList = new ArrayList<>();

		Arrays.asList(1, 2, 3, 4).forEach((i) -> {
			CompletableFuture<List<Student>> future = taskExecutor.submitListenable(() -> {
				return studentFilterService.filterWithoutTransactional(studentIdList, i);
			}).completable();

			futureList.add(future);
		});

		Thread.sleep(200);
		System.err.println("It will done immediatly. Will not wait end of future methods.");
		studentFilterService.easyTransactionalMethod();

		assertThat(futureList.stream().map(CompletableFuture::isDone)).contains(false);
	}

	@Test
	/*-
	 * 1. Don't do like this. Split you long life methods to short life @Transactional services.
	 */
	void transactional_long_life_methods() throws Exception {
		List<CompletableFuture<List<Student>>> futureList = new ArrayList<>();

		Arrays.asList(1, 2, 3, 4).forEach((i) -> {
			CompletableFuture<List<Student>> future = taskExecutor.submitListenable(() -> {
				return studentFilterService.filterTransactional(studentIdList, i);
			}).completable();

			futureList.add(future);
		});

		Thread.sleep(200);
		System.err.println("EasyMethod list can be started! Bu it will not because transactional blocked sessions.");
		System.err.println("It will wait end of future methods to unblock session.");
		studentFilterService.easyTransactionalMethod();

		assertThat(futureList.stream().map(CompletableFuture::isDone)).contains(true);
	}

}
