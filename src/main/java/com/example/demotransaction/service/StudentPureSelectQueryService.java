package com.example.demotransaction.service;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public Student getStudentTransactional(Integer id) {
		Student student = studentRepository.findById(id).get();
		return getStudent(student);
	}

	public Student getStudent(Student student) {
		Student student2 = studentRepository.findById(student.getId()).get();
		System.err.println("1:::::::" + student);
		System.err.println("2:::::::" + student);

		return student;
	}

}
