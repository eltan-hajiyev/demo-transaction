package com.example.demotransaction;

import com.example.demotransaction.model.Student;
import com.example.demotransaction.service.StudentCacheService;
import com.example.demotransaction.service.StudentQueryMixedWithProcessService;
import com.example.demotransaction.tools.CPUTime;
import com.example.demotransaction.tools.ThreadPoolExecutorForInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableCaching
@TestPropertySource(properties = {
        "spring.cache.type=redis",
        "spring.redis.host=localhost",
        "spring.redis.port=6379",
        "spring.cache.redis.time-to-live=6s",
        "spring.redis.timeout=1000",
})
public class DemoRedisCacheableTests {
    @Autowired
    StudentCacheService studentCacheService;

    @Autowired
    ThreadPoolExecutorForInfo threadPoolExecutorForInfo;

    @Test
    public void redis_get_and_cache() {
        studentCacheService.getAndEvictStudent(1);
        Long time1 = CPUTime.exec(() -> {
            Student student = studentCacheService.getStudent(1);
        });
        Long time2 = CPUTime.exec(() -> {
            Student student = studentCacheService.getStudent(1);
        });

        System.err.println(time1 + "::" + time2);
        assertThat(time1 / time2).isGreaterThan(100);
    }

    @Test
    public void redis_get_and_put() {
        studentCacheService.getAndEvictStudent(1);
        Long time1 = CPUTime.exec(() -> {
            Student student = studentCacheService.getStudent(1);
        });
        Long time2 = CPUTime.exec(() -> {
            Student student = studentCacheService.getAndPutStudent(1);
        });

        System.err.println(time1 + "::" + time2);
        assertThat(Math.abs(time1 - time2)).isLessThan(100);
    }
}
