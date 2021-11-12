package framework.discovery_mechanism;

import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassFinder {
    Object agent;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new ClassFinder();
    }

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


    @SuppressWarnings({"unchecked", "unchecked", "rawtypes"})
    private void processClass(String dirPath, File classFile) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fileName = classFile.getCanonicalPath();
        fileName = fileName.replace(dirPath, "");
        //System.out.println("Processed file: " + fileName);
        String className = filterName(fileName);
        //System.out.println("Qualified name: " +  className);
        Class cl = Class.forName(className);
        if (cl.getAnnotation(Controller.class) != null) {
            this.agent = cl.getDeclaredConstructor().newInstance();
            processMethods(cl);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private void processMethods(Class cl) throws InvocationTargetException, IllegalAccessException {
        for (Method m : cl.getDeclaredMethods()) {
            //primera radi
            GET get = m.getAnnotation(GET.class);
            POST post = m.getAnnotation(POST.class);
            Path path = m.getAnnotation(Path.class);
            if (path != null && (get != null || post != null)) {
                System.out.println("METHOD: " + m);
                System.out.println("MESSAGE TYPE: " + path.path());
                m.invoke(this.agent);
            }
        }
    }

    private String filterName(String path) {
        path = path.replace(".class", "");
        String filterPath = path.replace(File.separator, ".");
        filterPath = filterPath.replace("http.target.classes.", "");
        return filterPath.substring(1);
    }
}
