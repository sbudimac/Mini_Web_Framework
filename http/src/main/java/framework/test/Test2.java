package framework.test;

import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.Path;
import framework.request.Request;
import framework.response.JsonResponse;
import framework.response.Response;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Test2 {
    @GET
    @Path(path = "/test2")
    public Response test(Request request) {
        System.out.println("Get metoda2 radi");
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
