package framework.discovery_mechanism;

import framework.annotations.route_registration.Controller;
import framework.annotations.route_registration.GET;
import framework.annotations.route_registration.POST;
import framework.annotations.route_registration.Path;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class JarScanner {
    Object agent;

    public static void main(String[] args) throws IOException {
        new JarScanner();
    }

    public JarScanner() throws IOException {
        System.out.println("ali ovaj da");
        File libDir = new File(".");
        System.out.println(libDir.getCanonicalPath());
        File[] jars = libDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        for (int i = 0; i < Objects.requireNonNull(jars).length; i++) {
            try {
                System.out.println("Found jar file: " + jars[i].getName());
                URL url = jars[i].toURI().toURL();
                DiscoveryClassLoader.addURL(url);
                scanJar(jars[i], libDir.getCanonicalPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scanJar(File jar, String dirPath) throws IOException {
        List<File> content = listContent(jar);
        for (File f: content) {
            System.out.println("Found class file: " + f.getCanonicalPath());
            try {
                processClass(dirPath, f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<File> listContent(File file) throws ZipException, IOException {
        List<File> content = new ArrayList<>();
        ZipFile zf = new ZipFile(file);
        Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zf.entries();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement().getName();
            if (name.endsWith(".class")) {
                content.add(new File(name));
            }
        }
        zf.close();
        return content;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void processClass(String dirPath, File classFile) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fileName = classFile.getCanonicalPath();
        fileName = fileName.replace(dirPath, "");
        System.out.println("Processed file: " + fileName);
        String className = filterName(fileName);
        System.out.println("Qualified name: " +  className);
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
        return filterPath.substring(1);
    }
}
