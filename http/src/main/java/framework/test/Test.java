package framework.test;

import framework.annotations.dependency_injection.Autowired;
import framework.annotations.dependency_injection.Qualifier;
import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;

@Controller
public class Test {
    @Autowired(verbose = true)
    private DITest diTest;

    @Autowired(verbose = true)
    @Qualifier("implementation")
    private ITest iTest;

    @Autowired(verbose = true)
    @Qualifier("implementation")
    private ITest iTest2;

    @Autowired(verbose = true)
    private ImplementationTest implementationTest;

    @GET
    @Path(path = "/test")
    public void test() {
        System.out.println("Get metoda radi");
    }

    @POST
    @Path(path = "/test")
    public void test2() {
        System.out.println("Post metoda radi");
    }
}
