package com.example.demotransaction.controller;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/rest")
public class StudentRestController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/students/{id}")
    @Transactional
    public Student getStudents(@PathVariable Integer id, ModelAndView model) throws Exception {
        //Thread.sleep(20000);
        Student student = studentRepository.findById(id).get();

        return student;
    }
}
