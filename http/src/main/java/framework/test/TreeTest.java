package framework.test;

import framework.annotations.dependency_injection.*;
import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.Path;

@Component
public class TreeTest {
    @Autowired(verbose = true)
    private RootTest rootTest;
}
