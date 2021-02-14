package com.example.demotransaction.service;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.repository.StudentRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "redis-test-cache")
public class StudentCacheService {

    @Autowired
    StudentRepository studentRepository;

    @CachePut
    @Transactional
    public Student getAndPutStudent(Integer id) {
        return getStudentProcess(id);
    }

    @CacheEvict
    @Transactional
    public void getAndEvictStudent(Integer id) {
    }

    @Cacheable
    @Transactional
    public Student getStudent(Integer id) {
        return getStudentProcess(id);
    }

    private Student getStudentProcess(Integer id) {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
        return studentRepository.findById(id).get();
    }

}
