package com.example.demotransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import com.example.demotransaction.service.StudentService;

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
class DemoTransactionalTests {
	@Autowired
	private StudentRepository studentRepository;

	private StudentService studentService;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	public DemoTransactionalTests(StudentService studentService) {
		this.studentService = studentService;
	}

	@Test
	/*-
	 * Using: without any annotation
	 * 1. multiple session. will open new session for every query 
	 * 2. calling select before update for the checking changes
	 * 3. It will work with 'enable_lazy_load_no_trans=true' or with EAGER loading
	 */
	void testDefaultSelectAndUpdate() throws Exception {
		Integer studentId = 1;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentDefault(studentId);
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
	void testTransactionalSelectAndUpdate() throws Exception {
		Integer studentId = 2;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactional(studentId);
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
	 * 1: Like in default @Transactional but ignores update and insert. Will not throw exception if use save.
	 * 
	 */
	void testTransactionalReadOnlySelectAndUpdate() throws Exception {
		Integer studentId = 3;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactionalReadOnly(studentId);
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
	void testTransactionalLockSelectAndUpdate() throws Exception {
		Integer studentId = 4;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactionalLocked(studentId);
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
	void testTransactionalLockTimeoutSelectAndUpdate() throws Exception {
		Integer studentId = 5;
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactionalTimeoutLocked(studentId);
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
	void testNoTransactionalLockSelectAndUpdate() {
		Integer studentId = 1;
		assertThrows(Exception.class, () -> studentService.getStudentNoTransactionalLocked(studentId));
	}

}
