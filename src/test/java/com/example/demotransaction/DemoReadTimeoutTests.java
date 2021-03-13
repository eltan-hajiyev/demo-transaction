package com.example.demotransaction;

import com.example.demotransaction.controller.ReadTimeoutController;
import com.example.demotransaction.tools.CPUTime;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.utils.URIBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoReadTimeoutTests {

    private static Integer READ_TIMEOUT_LESS_THAN_OUT_INTERVAL = (int) Duration
            .ofSeconds(ReadTimeoutController.DATA_OUT_INTERVAL_SEC)
            .minusSeconds(1)
            .toMillis();

    private static Integer READ_TIMEOUT_GRATE_THAN_OUT_INTERVAL = (int) Duration
            .ofSeconds(ReadTimeoutController.DATA_OUT_INTERVAL_SEC)
            .plusSeconds(1)
            .toMillis();

    private URI uri;

    public DemoReadTimeoutTests(@LocalServerPort int port) throws Exception {
        uri = new URIBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(port)
                .setPath("/timeout/read")
                .build();
    }

    @Test
    void restTemplateTest() throws Exception {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //by default it's true. Recommended value is false.
        factory.setBufferRequestBody(false);
        RestTemplate restTemplate = new RestTemplate(factory);

        factory.setReadTimeout(READ_TIMEOUT_LESS_THAN_OUT_INTERVAL);
        long time1 = CPUTime.exec(() -> {
            Exception e = assertThrows(Exception.class, () -> {
                restTemplate.getForEntity(uri, String.class);
            });

            Assertions.assertThat(e.getMessage()).contains("Read timed out");
        });

        factory.setReadTimeout(READ_TIMEOUT_GRATE_THAN_OUT_INTERVAL);
        long time2 = CPUTime.exec(() -> {
            assertDoesNotThrow(() -> {
                restTemplate.getForEntity(uri, String.class);
            });
        });

        assertTrue(time1 < time2);
    }

    @Test
    void okHttpTest() throws Exception {
        System.out.println(uri.getRawPath());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(uri.toURL())
                .build();

        client.setReadTimeout(READ_TIMEOUT_LESS_THAN_OUT_INTERVAL, TimeUnit.MILLISECONDS);
        long time1 = CPUTime.exec(() -> {
            assertThrows(SocketTimeoutException.class, () -> {
                client.newCall(request).execute();
            });
        });

        client.setReadTimeout(READ_TIMEOUT_GRATE_THAN_OUT_INTERVAL, TimeUnit.MILLISECONDS);
        long time2 = CPUTime.exec(() -> {
            assertDoesNotThrow(() -> {
                client.newCall(request).execute();
            });
        });

        assertTrue(time1 < time2);
    }

}
