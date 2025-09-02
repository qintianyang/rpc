package com.kk.client;

import com.kk.client.controller.ClientController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ClientTest {
    @Resource
    ClientController clientController;
    // @Test
    // public void test() {
    //     clientController.test();
    // }
}