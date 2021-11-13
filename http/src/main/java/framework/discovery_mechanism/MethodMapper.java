package framework.discovery_mechanism;

import java.lang.reflect.Method;

import framework.request.enums.HttpMethod;

public class MethodMapper {
    private final HttpMethod httpMethod;
    private final String path;
    private final Object controller;
    private final Method method;

    public MethodMapper (HttpMethod httpMethod, String path, Object controller, Method method) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.controller = controller;
        this.method = method;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
