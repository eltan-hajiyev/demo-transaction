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
public class StudentQueryMixedWithProcessService {
	@Autowired
	StudentRepository studentRepository;
	private AtomicInteger atomicInteger = new AtomicInteger(0);

	public Student getStudentDefault(Integer id) {
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
		System.err.println("1:::::::" + student);

		if (student.getCount() > 0) {
			return student;
		}
		try {
			Thread.sleep(5000); //rest request
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		student.setCount(atomicInteger.incrementAndGet());
		student.setStatus(true);

		studentRepository.save(student);
		System.err.println("2:::::::" + student);

		return student;
	}

}
