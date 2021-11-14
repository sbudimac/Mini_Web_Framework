package framework.test;

import framework.annotations.dependency_injection.*;

@Component
public class TreeTest {
    @Autowired(verbose = true)
    private RootTest rootTest;
}
