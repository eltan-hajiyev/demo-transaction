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
    public void mvc_open_in_view() throws Exception {
        System.err.println("open-in-view works for MVC view.");
        try {
            mockMvc.perform(get("/mvc/students/1"));
            System.err.println("open-in-view=true");
        } catch (Throwable t) {
            System.err.println("open-in-view=false");
        }
    }

    @Test
    public void rest_open_in_view() throws Exception {
        System.err.println("open-in-view works for Rest service view.");
        try {
            mockMvc.perform(get("/rest/students/1")).andExpect((e) -> {
                System.err.println("response:"+e.getResponse().getContentAsString());
                System.err.println("open-in-view=false will return main object.");
            });
        } catch (Throwable t) {
            System.err.println("open-in-view=true will try to create full object model.");
        }
    }
}
