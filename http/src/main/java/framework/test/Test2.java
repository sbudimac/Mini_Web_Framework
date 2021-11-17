package framework.test;

import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.Path;

@Controller
public class Test2 {
    @GET
    @Path(path = "/test2")
    public void test() {
        System.out.println("Get metoda2 radi");
    }
}
