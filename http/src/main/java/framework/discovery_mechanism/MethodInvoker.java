package framework.discovery_mechanism;

import framework.request.Request;
import framework.request.enums.HttpMethod;
import framework.response.Response;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MethodInvoker {
    public static List<MethodMapper> mappers = new ArrayList<>();

    public static Response invokeMethod(HttpMethod method, Request request) throws InvocationTargetException, IllegalAccessException {
        for (MethodMapper mapper : mappers) {
            if (mapper.getHttpMethod().equals(method) && mapper.getPath().equals(request.getLocation())) {
                return (Response) mapper.getMethod().invoke(mapper.getController(), request);
            }
        }
        return null;
    }

    public static boolean mapperExists(HttpMethod method, String path, Object controller) {
        for (MethodMapper mapper : mappers) {
            if (mapper.getHttpMethod().equals(method) && mapper.getPath().equals(path) && mapper.getController().equals(controller)) {
                return true;
            }
        }
        return false;
    }
}
