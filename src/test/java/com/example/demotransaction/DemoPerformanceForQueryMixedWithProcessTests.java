package com.example.demotransaction;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.service.StudentQueryMixedWithProcessService;
import com.example.demotransaction.tools.ThreadPoolExecutorForInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Not recommended way
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.hikari.maximum-pool-size=2",
        "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"
})
public class DemoPerformanceForQueryMixedWithProcessTests {
    @Autowired
    StudentQueryMixedWithProcessService studentQueryMixedWithProcessService;

    Integer studentId = 1;
    int threadCount = 20;

    @Autowired
    ThreadPoolExecutorForInfo threadPoolExecutorForInfo;

    @Test
    public void transactional() {
        threadPoolExecutorForInfo.execute(threadCount, () -> {
            Student student = studentQueryMixedWithProcessService.getStudentTransactional(studentId);
            return student;
        });

        System.out.println(threadPoolExecutorForInfo);
    }

    @Test
    public void none_transactional() {
        threadPoolExecutorForInfo.execute(threadCount, () -> {
            Student student = studentQueryMixedWithProcessService.getStudentDefault(studentId);
            return student;
        });

        System.out.println(threadPoolExecutorForInfo);
    }

}
