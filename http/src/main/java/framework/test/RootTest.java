package framework.test;

import framework.annotations.dependency_injection.Bean;

@Bean
public class RootTest {
    public RootTest() {
        System.out.println("Root test injected");
    }
}
