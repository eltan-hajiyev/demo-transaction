package com.example.demotransaction.controller;

import java.io.PrintWriter;
import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeout")
public class ReadTimeoutController {
    public static int DATA_OUT_INTERVAL_SEC = 5;

    @GetMapping("/read")
    public void partialReturn(PrintWriter writer) throws Exception {
        String[] partialResponse = "Hello my dear friend.".split(" ");

        for (String r : partialResponse) {
            writer.write(r);
            writer.write(" ");
            writer.flush();
            Thread.sleep(Duration.ofSeconds(DATA_OUT_INTERVAL_SEC).toMillis());
        }
    }
}
