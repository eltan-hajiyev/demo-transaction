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

    private static Integer READ_TIMEOUT_LESS_THAN_OUT_INTERVAL_SEC =
            ReadTimeoutController.DATA_OUT_INTERVAL_SEC - 1;

    private static Integer READ_TIMEOUT_GRATE_THAN_OUT_INTERVAL_SEC =
            ReadTimeoutController.DATA_OUT_INTERVAL_SEC + 1;

    private static Integer STANDARD_DEVIATION_MILLIE = 150;

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

        int readTimeout1 = (int)Duration.ofSeconds(READ_TIMEOUT_LESS_THAN_OUT_INTERVAL_SEC).toMillis();
        factory.setReadTimeout(readTimeout1);
        long time1 = CPUTime.exec(() -> {
            Exception e = assertThrows(Exception.class, () -> {
                restTemplate.getForEntity(uri, String.class);
            });

            Assertions.assertThat(e.getMessage()).contains("Read timed out");
        });
        assertTrue(time1 < readTimeout1 + STANDARD_DEVIATION_MILLIE);

        int readTimeout2 = (int)Duration.ofSeconds(READ_TIMEOUT_GRATE_THAN_OUT_INTERVAL_SEC).toMillis();
        factory.setReadTimeout(readTimeout2);
        long time2 = CPUTime.exec(() -> {
            assertDoesNotThrow(() -> {
                restTemplate.getForEntity(uri, String.class);
            });
        });

        assertFalse(time2 < 3 * readTimeout2);
    }

    @Test
    void okHttpTest() throws Exception {
        System.out.println(uri.getRawPath());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(uri.toURL())
                .build();

        client.setReadTimeout(READ_TIMEOUT_LESS_THAN_OUT_INTERVAL_SEC, TimeUnit.SECONDS);
        long time1 = CPUTime.exec(() -> {
            assertThrows(SocketTimeoutException.class, () -> {
                String res = client.newCall(request).execute().body().string();
            });
        });
        assertTrue(time1 < client.getReadTimeout() + STANDARD_DEVIATION_MILLIE);

        client.setReadTimeout(READ_TIMEOUT_GRATE_THAN_OUT_INTERVAL_SEC, TimeUnit.SECONDS);
        long time2 = CPUTime.exec(() -> {
            assertDoesNotThrow(() -> {
                String res = client.newCall(request).execute().body().string();
            });
        });

        assertFalse(time2 < 3 * client.getReadTimeout());
    }

}
