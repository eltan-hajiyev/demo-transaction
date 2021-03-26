package com.example.demotransaction;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import com.example.demotransaction.service.StudentQueryMixedWithProcessService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
@TestPropertySource(properties = { "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true" })
class DemoTransactionalForMixedProcessTests {
	@Autowired
	private StudentRepository studentRepository;

	private StudentQueryMixedWithProcessService studentQueryMixedWithProcessService;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	public DemoTransactionalForMixedProcessTests(StudentQueryMixedWithProcessService studentQueryMixedWithProcessService) {
		this.studentQueryMixedWithProcessService = studentQueryMixedWithProcessService;
	}

	@Test
	/*-
	 * Using: without any annotation
	 * 1. multiple session. will open new session for every query 
	 * 2. calling select before update for the checking changes
	 * 3. It will work with 'enable_lazy_load_no_trans=true' or with EAGER loading
	 */
	void default_select_update() throws Exception {
		Integer studentId = 1;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentQueryMixedWithProcessService.getStudentDefault(studentId);
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentId).get();
		System.err.println("count:" + student.getCount());
		assertThat(student.getCount()).isNotEqualTo(1);
	}

	@Test
	/*-
	 * Using: @Transactional
	 * 
	 * 1: opens single session.
	 * 2: will not call select before update
	 * 3. with max-poll-size=1 it will work like @Transactional with @Lock.
	 */
	void transactional_select_update() throws Exception {
		Integer studentId = 2;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentQueryMixedWithProcessService.getStudentTransactional(studentId);
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentId).get();
		System.err.println("count:" + student.getCount());
		assertThat(student.getCount()).isNotEqualTo(1);
	}

	@Test
	/*-
	 * Using: @Transactional(readOnly = true)
	 * 
	 * 1. Like in default @Transactional but ignores update and insert. Will not throw exception if use save.
	 * 2. No dirty reads, no unrepeatable reads, doesn’t allow any updates, flush mode will be FlushMode.NEVER.
	 * 3. You cannot call session.flsuh() even to flush session manually.
	 */
	void transactional_ReadOnly_select_update() throws Exception {
		Integer studentId = 3;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentQueryMixedWithProcessService.getStudentTransactionalReadOnly(studentId);
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentId).get();
		System.err.println("count:" + student.getCount());
		assertThat(student.getCount()).isEqualTo(0);
	}

	@Test
	/*-
	 * Using: @Transactional, @Lock(PESSIMISTIC_READ)
	 * 
	 * 1. Row will be locked till end of transaction 
	 * 2. Will not call select before update
	 */
	void transactional_lock_select_update() throws Exception {
		Integer studentId = 4;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentQueryMixedWithProcessService.getStudentTransactionalLocked(studentId);
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentId).get();
		System.err.println("count:" + student.getCount());
		assertThat(student.getCount()).isEqualTo(1);
	}

	@Test
	/*-
	 * Using: @Transactional(timeout = 2), @Lock(PESSIMISTIC_READ)
	 * 
	 * 1. Row will be locked till end of transaction 
	 * 2. Will not call select before update
	 * 3. Will close connection after 2 second and will throw exception for next 
	 */
	void transactional_lock_timeout_select_update() throws Exception {
		Integer studentId = 5;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentQueryMixedWithProcessService.getStudentTransactionalTimeoutLocked(studentId);
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		assertThrows(Exception.class, () -> futureList.stream().forEach(f -> f.join()));
	}

	@Test
	/*-
	 * Using: @Lock without @Transactional
	 * 
	 * 1. Lock will not work without @Transactional
	 */
	void test_NoTransactional_lock_select_update() {
		Integer studentId = 1;
		assertThrows(Exception.class, () -> studentQueryMixedWithProcessService.getStudentNoTransactionalLocked(studentId));
	}

}
