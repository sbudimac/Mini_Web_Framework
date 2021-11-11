package framework.discovery_mechanism;


import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DiscoveryClassLoader {
    private static final Class[] parameters = new Class[] {URL.class};

    public static void addURL(URL url) throws IOException {
        URLClassLoader systemLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class systemClass = URLClassLoader.class;
        try {
            Method method = systemClass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(systemLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
