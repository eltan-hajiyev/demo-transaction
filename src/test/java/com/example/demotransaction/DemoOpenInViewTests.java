package com.example.demotransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        //you can check it by changing value
        "spring.jpa.open-in-view=false"
})
public class DemoOpenInViewTests {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void open_in_view() throws Exception {
        System.err.println("MVC open-in-view works for for MVC view.");
        try {
            mockMvc.perform(get("/students/1"));
            System.err.println("open-in-view enabled. spring.jpa.open-in-view=true");
        } catch (Throwable t) {
            System.err.println("open-in-view disabled. spring.jpa.open-in-view=false");
        }
    }
}
