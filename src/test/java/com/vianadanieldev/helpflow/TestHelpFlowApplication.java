package com.vianadanieldev.helpflow;

import org.springframework.boot.SpringApplication;

public class TestHelpFlowApplication {

    public static void main(String[] args) {
        SpringApplication.from(HelpFlowApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
