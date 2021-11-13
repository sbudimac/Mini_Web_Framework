package framework.discovery_mechanism;

import framework.annotations.dependency_injection.Autowired;
import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;
import framework.request.enums.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassFinder {
    Object agent;

    public ClassFinder() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        File libDir = new File(".");
        List<File> content = new ArrayList<>();
        scanFiles(content, Objects.requireNonNull(libDir.listFiles()));
        for (File f : content) {
            processClass(libDir.getCanonicalPath(), f);
        }
    }

    public void scanFiles(List<File> content, File[] files) {
        for (File file: files) {
            if (file.isDirectory()) {
                scanFiles(content, Objects.requireNonNull(file.listFiles()));
            } else {
                if (file.getName().endsWith(".class")) {
                    content.add(file);
                }
            }
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private void processClass(String dirPath, File classFile) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fileName = classFile.getCanonicalPath();
        fileName = fileName.replace(dirPath, "");
        String className = filterName(fileName);
        Class cl = Class.forName(className);
        if (cl.getAnnotation(Controller.class) != null) {
            this.agent = cl.getDeclaredConstructor().newInstance();
            processMethods(cl);
            processFields(cl, this.agent);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private void processMethods(Class cl) throws InvocationTargetException, IllegalAccessException {
        for (Method m : cl.getDeclaredMethods()) {
            GET get = m.getAnnotation(GET.class);
            POST post = m.getAnnotation(POST.class);
            Path path = m.getAnnotation(Path.class);
            if (path != null && (get != null || post != null)) {
                HttpMethod httpMethod = null;
                if (get != null) {
                    httpMethod = HttpMethod.GET;
                } else {
                    httpMethod = HttpMethod.POST;
                }
                if (MethodInvoker.mapperExists(httpMethod, path.path(), this.agent)) {
                    System.out.println("Two methods with same http method and path in the same controller.");
                    System.exit(0);
                }
                MethodMapper mm = new MethodMapper(httpMethod, path.path(), this.agent, m);
                MethodInvoker.mappers.add(mm);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void processFields(Class cl, Object agent) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        for (Field f : cl.getDeclaredFields()) {
            Autowired autowired = f.getAnnotation(Autowired.class);
            if (autowired != null) {
                f.setAccessible(true);
                Class fClass = Class.forName(getClassName(f));
                Object obj = fClass.getDeclaredConstructor().newInstance();
                processFields(fClass, obj);
                f.set(agent, obj);
                if (autowired.verbose()) {
                    System.out.println("Initialized " + f.getType() + " " + f.getName() + " in " + obj.getClass() + " on " + LocalDateTime.now() + " with " + obj.hashCode());
                }
            }
        }
    }

    private String filterName(String path) {
        path = path.replace(".class", "");
        String filterPath = path.replace(File.separator, ".");
        filterPath = filterPath.substring(filterPath.indexOf("classes") - 1);
        filterPath = filterPath.replace("classes.", "");
        return filterPath.substring(1);
    }

    private String getClassName(Field f) {
        String fullName = String.valueOf(f.getType());
        String[] parts = fullName.split(" ");
        return parts[parts.length - 1];
    }
}
