package com.kynn.reevo_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class ModulithArchitectureTest {

    @Test
    void should_verify_modules() {
        var modules = ApplicationModules.of(ReevoBackendApplication.class);
        modules.verify();
    }
}