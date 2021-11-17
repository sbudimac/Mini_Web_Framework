package framework.di;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {
    private static final Map<String, Object> implementations = new HashMap<>();

    public static boolean hasImplementation(String impl) {
        if (implementations.get(impl) == null) {
            throw new RuntimeException("Ne postoji implementacija");
        } else {
            return true;
        }
    }

    public static Object getImplemetnation(String impl) {
        return implementations.get(impl);
    }

    public static Map<String, Object> getImplementations() {
        return implementations;
    }
}
