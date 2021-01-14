package com.example.demotransaction.controller;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

@RestController
@EnableCaching
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public Iterable<Student> getStudents() throws Exception {
        Thread.sleep(20000);
        return studentRepository.findAll();
    }
}
