package com.example.demotransaction.controller;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;

@Controller
public class StudentMvcController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/students/{id}")
    @Transactional
    public ModelAndView getStudents(@PathVariable Integer id, ModelAndView model) throws Exception {
        //Thread.sleep(20000);
        Student student = studentRepository.findById(id).get();

        model.setViewName("students");
        model.addObject("student", student);

        return model;
    }
}
