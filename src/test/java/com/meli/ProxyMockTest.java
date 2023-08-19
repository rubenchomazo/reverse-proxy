package com.meli;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProxyMockTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnOk() throws Exception {
        this.mockMvc.perform(get("/categories/MLA1512")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Agro")));
    }

    @Test
    public void shouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/categories/MLA97994")).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Category not found")));
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("resource not found")));
    }

}
