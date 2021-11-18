package framework.test;

import framework.annotations.dependency_injection.Autowired;
import framework.annotations.dependency_injection.Qualifier;
import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;
import framework.request.Request;
import framework.response.JsonResponse;
import framework.response.Response;

import java.util.HashMap;
import java.util.Map;

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
    public Response test(Request request) {
        System.out.println("Get metoda radi");
        return getResponse(request);
    }

    @POST
    @Path(path = "/test")
    public Response test2(Request request) {
        System.out.println("Post metoda radi");
        return getResponse(request);
    }

    private Response getResponse(Request request) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        return new JsonResponse(responseMap);
    }
}
