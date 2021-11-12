package framework.discovery_mechanism;

import framework.request.enums.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MethodInvoker {
    static List<MethodMapper> mappers = new ArrayList<>();

    public static void invokeMethod(HttpMethod method, String path) throws InvocationTargetException, IllegalAccessException {
        for (MethodMapper mapper : mappers) {
            if (mapper.getHttpMethod().equals(method) && mapper.getPath().equals(path)) {
                mapper.getMethod().invoke(mapper.getController());
            }
        }
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
