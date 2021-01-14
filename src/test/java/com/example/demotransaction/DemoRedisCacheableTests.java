package com.example.demotransaction;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.service.StudentService;
import com.example.demotransaction.tools.CPUTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableCaching
@TestPropertySource(properties = {
        "spring.cache.type=redis",
        "spring.redis.host=localhost",
        "spring.redis.port=6379"
})
public class DemoRedisCacheableTests {
    @Autowired
    StudentService studentService;

    @Test
    @Transactional
    public void redis_cacheable() {
        Long time1 = CPUTime.exec(()->{
            Student student = studentService.getStudentCacheable(1);
        });
        Long time2 = CPUTime.exec(() -> {
            Student student = studentService.getStudentCacheable(1);
        });

        System.err.println(time1 + "::" + time2);
        assertThat(time1 / time2).isGreaterThan(100);
    }
}
