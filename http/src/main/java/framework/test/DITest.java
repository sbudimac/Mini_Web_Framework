package framework.test;

import framework.annotations.dependency_injection.Autowired;

public class DITest {
    @Autowired(verbose = true)
    private TreeTest treeTest;

    public DITest() {
        System.out.println("DITest injected");
    }
}
