package com.meli;

import com.meli.controller.ProxyController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ReverseProxyApplicationTests {

    @Autowired
    private ProxyController proxyController;

    @Test
    void contextLoads() {
        assertThat(proxyController).isNotNull();
    }

}
