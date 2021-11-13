package framework.test;

import framework.annotations.dependency_injection.Autowired;
import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;

@Controller
public class Test {
    @Autowired(verbose = true)
    private DITest diTest;

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
