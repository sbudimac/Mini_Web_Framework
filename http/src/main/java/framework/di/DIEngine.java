package framework.di;

import framework.annotations.dependency_injection.*;
import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;
import framework.discovery_mechanism.MethodInvoker;
import framework.discovery_mechanism.MethodMapper;
import framework.request.enums.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

public class DIEngine {
    private Object agent;
    private final Map<String, Object> singletons;

    public DIEngine() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        singletons = new HashMap<>();
        File libDir = new File(".");
        List<File> content = new ArrayList<>();
        scanFiles(content, Objects.requireNonNull(libDir.listFiles()));
        for (File f : content) {
            generateImplementations(libDir.getCanonicalPath(), f);
        }
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
    private void generateImplementations(String dirPath, File classFile) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fileName = classFile.getCanonicalPath();
        fileName = fileName.replace(dirPath, "");
        String className = filterName(fileName);
        Class cl = Class.forName(className);
        if (cl.getAnnotation(Qualifier.class) != null) {
            Qualifier qualifier = (Qualifier) cl.getAnnotation(Qualifier.class);
            DependencyContainer.implementations.put(qualifier.value(), cl.getDeclaredConstructor().newInstance());
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
                if (fClass.getAnnotation(Bean.class) == null && fClass.getAnnotation(Service.class) == null && fClass.getAnnotation(Component.class) == null && f.getAnnotation(Qualifier.class) == null) {
                    System.out.println("Autowired field class is not a bean.");
                    System.exit(0);
                }
                Object obj = null;
                if (fClass.getAnnotation(Bean.class) != null) {
                    Bean bean = (Bean) fClass.getAnnotation(Bean.class);
                    if (bean.scope().equals(Scope.SINGLETON)) {
                        if (singletons.get(fClass.getName()) == null) {
                            obj = fClass.getDeclaredConstructor().newInstance();
                            singletons.put(fClass.getName(), obj);
                        } else {
                            obj = singletons.get(fClass.getName());
                        }
                    } else if (bean.scope().equals(Scope.PROTOTYPE)) {
                        obj = fClass.getDeclaredConstructor().newInstance();
                    }
                } else if (fClass.getAnnotation(Service.class) != null) {
                    if (singletons.get(fClass.getName()) == null) {
                        obj = fClass.getDeclaredConstructor().newInstance();
                        singletons.put(fClass.getName(), obj);
                    } else {
                        obj = singletons.get(fClass.getName());
                    }
                } else if (fClass.getAnnotation(Component.class) != null) {
                    obj = fClass.getDeclaredConstructor().newInstance();
                } else if (f.getAnnotation(Qualifier.class) != null) {
                    Qualifier qualifier = f.getAnnotation(Qualifier.class);
                    obj = DependencyContainer.implementations.get(qualifier.value());
                }
                processFields(fClass, obj);
                f.set(agent, obj);
                if (autowired.verbose()) {
                    assert obj != null;
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
