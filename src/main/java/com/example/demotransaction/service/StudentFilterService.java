package com.example.demotransaction.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;

@Service
public class StudentFilterService {
	@Autowired
	StudentRepository studentRepository;

	public List<Student> filterWithoutTransactional(List<Integer> idList, Integer index) throws Exception {
		return filter(idList, index);
	}

	@Transactional
	public List<Student> filterTransactional(List<Integer> idList, Integer index) throws Exception {
		return filter(idList, index);
	}

	public List<Student> filter(List<Integer> idList, Integer index) throws Exception {
		System.err.println("filter1: " + index);

		Thread.sleep(5000); // Rest request

		Iterable<Student> studentIterable = studentRepository.findAllById(idList);

		List<Student> studentList = new ArrayList<>();
		studentIterable.forEach(v -> studentList.add(v));

		return studentList;
	}

	@Transactional
	public Integer easyTransactionalMethod() {
		System.err.println("EasyMethod done");
		return 5;
	}
}
