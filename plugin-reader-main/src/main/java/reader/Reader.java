package reader;

import pluginabstract.TestAnnotation;
import pluginabstract.TestInterface;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reader {

    private static final String CLASSPATH = System.getProperty("java.class.path");
    private static final String MAIN_JAR_LOCATION_CLASSPATH = CLASSPATH.split(";")[0];

    private static final File DIRECTORY = new File(MAIN_JAR_LOCATION_CLASSPATH).getParentFile();

    private List<String> pluginsFound;


    public void read() {
        pluginsFound = getPlugins(DIRECTORY);
        List<Class> allClassesFound = new LinkedList<>();

        try {
            findClasses(pluginsFound, allClassesFound);

            printAnnotationsValue(allClassesFound);

            invokeMethodsFromGivenInterface(allClassesFound, TestInterface.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findClasses(List<String> pluginsFound, List<Class> allClassesFound) throws IOException, ClassNotFoundException {
        for (String pluginName : pluginsFound) {
            allClassesFound.addAll(readClassesFromPlugin(pluginName));
        }
    }

    private List<Class> readClassesFromPlugin(String pathToJar) throws IOException, ClassNotFoundException {
        List<Class> pluginClasses = new LinkedList<>();

        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (isNotClass(je)) {
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');

            pluginClasses.add(cl.loadClass(className));
        }

        return pluginClasses;

    }

    private boolean isNotClass(JarEntry je) {
        return je.isDirectory() || !je.getName().endsWith(".class");
    }

    private void printAnnotationsValue(List<Class> allClassesFound) {
        for (Class aClass : allClassesFound) {
            if (aClass.isAnnotationPresent(TestAnnotation.class)) {
                TestAnnotation annotation = (TestAnnotation) aClass.getAnnotation(TestAnnotation.class);
                System.out.println(annotation.value());
            }
        }
    }

    private void invokeMethodsFromGivenInterface(List<Class> allClassesFound, Class testInterface) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String begin = "begin: ";
        for (Class aClass : allClassesFound) {
            for (Class anInterface : aClass.getInterfaces()) {
                if (anInterface.getName().contains(testInterface.getCanonicalName())) {
                    Method declaredMethod = getMethodToInvoke(aClass, anInterface);
                    begin = (String) declaredMethod.invoke(createInvokingObj(aClass), begin);
                }
            }
        }

        System.out.println(begin);
    }

    private Method getMethodToInvoke(Class aClass, Class anInterface) throws NoSuchMethodException {
        String methodNameFromInterface = anInterface.getDeclaredMethods()[0].getName();
        return aClass.getDeclaredMethod(methodNameFromInterface, String.class);
    }

    private Object createInvokingObj(Class aClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = aClass.getConstructor(null);
        return constructor.newInstance(null);
    }

    private List<String> getPlugins(File directory) {
        List<String> plugins = new LinkedList<>();
        System.out.println("path:");
        System.out.println(directory.getAbsolutePath());
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String name = file.getName();
            if (name.endsWith(".jar")) {
                plugins.add(directory + "\\" + name);
            }
        }
        return plugins;
    }
}
