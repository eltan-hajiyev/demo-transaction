package com.example.demotransaction;

import com.example.demotransaction.service.StudentPureSelectQueryService;
import com.example.demotransaction.tools.ThreadPoolExecutorForInfo;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EnableAspectJAutoProxy
@TestPropertySource(properties = {
        "spring.datasource.hikari.maximum-pool-size=1",
        "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"
})
public class DemoAopBehaviourWithTransactionalTests {
    @Autowired
    StudentPureSelectQueryService studentPureSelectQueryService;

    @Autowired
    ThreadPoolExecutorForInfo threadPoolExecutorForInfo;

    private static int THREAD_COUNT = 3;
    private static int AOP_PROCEDURE_TIME_SEC = 3;

    @TestConfiguration
    public static class AopTestConfiguration {
        @Aspect
        @Component
        public static class AspectConfig {
            @Pointcut("@within(org.springframework.stereotype.Service)")
            protected void serviceMethods() { }

            @AfterReturning("serviceMethods()")
            public void aroundServiceMethods() throws Throwable {
                System.out.println("started procedure inside AOP method");
                TimeUnit.SECONDS.sleep(AOP_PROCEDURE_TIME_SEC);
                System.out.println("finished procedure inside AOP method");
            }
        }
    }

    /**
     * AOP procedures will slow down your @Transactional methods
     */
    @Test
    void aop_procedure_keeps_open_transactional_session() {
        threadPoolExecutorForInfo.execute(THREAD_COUNT, () -> {
            return studentPureSelectQueryService.getStudentTransactional(1);
        });

        System.out.println(threadPoolExecutorForInfo);
        long difference = Math.abs(threadPoolExecutorForInfo.getMaxTime() - threadPoolExecutorForInfo.getMinTime());
        Assertions.assertThat(Duration.ofMillis(difference)).isGreaterThan(Duration.ofSeconds(AOP_PROCEDURE_TIME_SEC));
    }

    /**
     * AOP will not react to your none @Transactional methods
     */
    @Test
    void aop_procedure_not_keeps_none_transactional_session() {
        threadPoolExecutorForInfo.execute(THREAD_COUNT, () -> {
            return studentPureSelectQueryService.getStudentDefault(1);
        });

        System.out.println(threadPoolExecutorForInfo);
        long difference = Math.abs(threadPoolExecutorForInfo.getMaxTime() - threadPoolExecutorForInfo.getMinTime());
        Assertions.assertThat(Duration.ofMillis(difference)).isLessThan(Duration.ofSeconds(1));
    }
}
