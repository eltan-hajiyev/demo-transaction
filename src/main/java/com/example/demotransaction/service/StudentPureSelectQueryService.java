package com.example.demotransaction.service;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StudentPureSelectQueryService {
	@Autowired
	StudentRepository studentRepository;
	private AtomicInteger atomicInteger = new AtomicInteger(0);

	public Student getStudentDefault(Integer id) {
		Student student = studentRepository.findById(id).get();

		return getStudent(student);
	}

	@Cacheable("redis-test-cache")
	public Student getStudentCacheable(Integer id) {
		Student student = studentRepository.findById(id).get();

		return getStudent(student);
	}

	@Transactional
	public Student getStudentTransactional(Integer id) {
		Student student = studentRepository.findById(id).get();
		return getStudent(student);
	}

	@Transactional(readOnly = true)
	public Student getStudentTransactionalReadOnly(Integer id) {
		Student student = studentRepository.findById(id).get();
		return getStudent(student);
	}

	@Transactional
	public Student getStudentTransactionalLocked(Integer id) {
		Student student = studentRepository.findStudentForRead(id);
		return getStudent(student);
	}

	@Transactional(timeout = 2)
	public Student getStudentTransactionalTimeoutLocked(Integer id) {
		Student student = studentRepository.findStudentForRead(id);
		return getStudent(student);
	}

	public Student getStudentNoTransactionalLocked(Integer id) {
		Student student = studentRepository.findStudentForRead(id);
		return getStudent(student);
	}

	public Student getStudent(Student student) {
		Student student2 = studentRepository.findById(student.getId()).get();
		System.err.println("1:::::::" + student);
		System.err.println("2:::::::" + student);

		return student;
	}

}
