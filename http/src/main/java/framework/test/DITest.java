package framework.test;

import framework.annotations.dependency_injection.Autowired;
import framework.annotations.dependency_injection.Bean;

@Bean
public class DITest {
    @Autowired(verbose = true)
    private TreeTest treeTest;

    @Autowired(verbose = true)
    private TreeTest treeTest2;

    @Autowired(verbose = true)
    private TreeTest treeTest3;

    @Autowired(verbose = false)
    private TreeTest falseTest;

    public DITest() {
        System.out.println("DITest injected");
    }
}
