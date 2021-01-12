package com.example.demotransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
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
class DemoTransactionTests {
	@Autowired
	private StudentRepository studentRepository;

	private StudentService studentService;

	private static Student studentInitial;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	public DemoTransactionTests(StudentService studentService) {
		this.studentService = studentService;
	}

	@PostConstruct
	public void initSingleModel() {
		studentInitial = new Student();
		studentInitial.setName("Rustam");
		studentInitial.setStatus(false);

		System.out.println("initial creating student: ");
		studentRepository.save(studentInitial);
	}

	@Test
	/*-
	 * Using: without any annotation
	 * 1. new session for every query 
	 * 2. calling select before update for the checking changes
	 * 3. It will not work without 'enable_lazy_load_no_trans=true'
	 */
	void testDefaultSelectAndUpdate() throws Exception {
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentDefault(studentInitial.getId());
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentInitial.getId()).get();
		System.err.println("count:" + student.getCount());
		assertThat(student.getCount()).isNotEqualTo(1);
	}

	@Test
	/*-
	 * Using: @Transactional
	 * 
	 * 1: opens single session.
	 * 2: will not call select before update
	 * 3. starts transaction
	 * 4. with max-poll-size=1 it will work like @Transactional with @Lock.
	 */
	void testTransactionalSelectAndUpdate() throws Exception {
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactional(studentInitial.getId());
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentInitial.getId()).get();
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
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactionalReadOnly(studentInitial.getId());
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentInitial.getId()).get();
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
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactionalLocked(studentInitial.getId());
			}).completable();
			Thread.sleep(200);
			futureList.add(future);
		}

		futureList.stream().forEach(f -> f.join());

		Student student = studentRepository.findById(studentInitial.getId()).get();
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
		List<CompletableFuture<Student>> futureList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CompletableFuture<Student> future = taskExecutor.submitListenable(() -> {
				return studentService.getStudentTransactionalTimeoutLocked(studentInitial.getId());
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
		assertThrows(Exception.class, () -> studentService.getStudentNoTransactionalLocked(studentInitial.getId()));
	}

}
