package com.meli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProxyLoadTest {
    private final static Logger log = LogManager.getLogger(ProxyLoadTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnCategoriesTooManyRequest() throws Exception {
        for (int i = 1; i <= 1000; i++) {
            this.mockMvc.perform(get("/categories/MLA1512"));
            log.info("Call service categories attempt #:" + i);
        }
        this.mockMvc.perform(get("/categories/MLA1512")).andExpect(status().isTooManyRequests())
                .andExpect(content().string(containsString("Too Many Request")));
    }

    @Test
    public void shouldReturnSellsTooManyRequest() throws Exception {
        for (int i = 1; i <= 10; i++) {
            this.mockMvc.perform(get("/sells"));
            log.info("Call service sells attempt #:" + i);
        }
        this.mockMvc.perform(get("/sells")).andExpect(status().isTooManyRequests())
                .andExpect(content().string(containsString("Too Many Request")));
    }
}
