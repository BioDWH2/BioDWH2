package de.unibi.agbi.biodwh2.core;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Factory {
    private static final List<String> IGNORED_JARS = Arrays.asList("rt.jar", "idea_rt.jar", "aws-java-sdk-ec2",
                                                                   "proto-", "google-cloud-", "google-api-",
                                                                   "openstack4j-core", "selenium-", "google-api-client",
                                                                   "jackson-", "guava", "jetty", "netty-", "junit-");
    private static Factory instance;
    private final Map<String, List<Class<?>>> interfaceToImplementationsMap;
    private final Map<String, List<Class<?>>> baseClassToImplementationsMap;
    private final Set<String> allClassPaths;

    private Factory() {
        interfaceToImplementationsMap = new HashMap<>();
        baseClassToImplementationsMap = new HashMap<>();
        allClassPaths = new HashSet<>();
        collectAllClassPaths();
        loadAllClasses();
    }

    public static Factory getInstance() {
        return instance != null ? instance : (instance = new Factory());
    }

    private void loadAllClasses() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        for (String classPath : allClassPaths)
            loadClassPath(classLoader, classPath);
    }

    private void collectAllClassPaths() {
        String runtimeClassPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        for (String classPath : runtimeClassPath.split(File.pathSeparator)) {
            File file = new File(classPath);
            if (file.isDirectory())
                iterateFileSystem(file, file.toURI().toString());
            else if (isValidJarFile(file))
                iterateJarFile(file);
        }
    }

    private static boolean isValidJarFile(File file) {
        String fileName = file.getName().toLowerCase(Locale.US);
        return file.isFile() && fileName.endsWith(".jar") && IGNORED_JARS.stream().noneMatch(fileName::contains);
    }

    private void iterateFileSystem(File directory, String rootPath) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    iterateFileSystem(file, rootPath);
                else if (file.isFile())
                    addUriIfValidClassPath(file.toURI().toString().substring(rootPath.length()));
            }
        }
    }

    private void addUriIfValidClassPath(String uri) {
        if (isUriClassInBioDWH(uri))
            allClassPaths.add(getClassPathFromUri(uri));
    }

    private void iterateJarFile(File file) {
        Enumeration<JarEntry> je = tryGetJarFileEntries(file);
        while (je.hasMoreElements()) {
            JarEntry j = je.nextElement();
            if (!j.isDirectory())
                addUriIfValidClassPath(j.getName());
        }
    }

    private Enumeration<JarEntry> tryGetJarFileEntries(File file) {
        try {
            return new JarFile(file).entries();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyEnumeration();
        }
    }

    private static boolean isUriClassInBioDWH(String uri) {
        return uri.endsWith(".class") && uri.contains("de/unibi/agbi/biodwh2");
    }

    private static String getClassPathFromUri(String uri) {
        return uri.replace("/", ".").replace(".class", "");
    }

    private void loadClassPath(ClassLoader classLoader, String classPath) {
        Class<?> c = tryLoadClass(classLoader, classPath);
        if (c != null) {
            linkClassToParentInterfaces(c);
            linkClassToSuperclass(c);
        }
    }

    private static Class<?> tryLoadClass(ClassLoader classLoader, String classPath) {
        try {
            return classLoader.loadClass(classPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void linkClassToParentInterfaces(Class<?> c) {
        for (Class<?> classInterface : c.getInterfaces())
            linkClassToParentInterface(c, classInterface.getName());
    }

    private void linkClassToParentInterface(Class<?> c, String interfaceName) {
        if (!interfaceToImplementationsMap.containsKey(interfaceName))
            interfaceToImplementationsMap.put(interfaceName, new ArrayList<>());
        interfaceToImplementationsMap.get(interfaceName).add(c);
    }

    private void linkClassToSuperclass(Class<?> c) {
        if (c.getSuperclass() != null) {
            String superclassName = c.getSuperclass().getName();
            if (!baseClassToImplementationsMap.containsKey(superclassName))
                baseClassToImplementationsMap.put(superclassName, new ArrayList<>());
            baseClassToImplementationsMap.get(superclassName).add(c);
        }
    }

    public <T> List<Class<T>> getImplementations(Class<T> type) {
        String typeName = type.getName();
        if (interfaceToImplementationsMap.containsKey(typeName))
            return mapImplementationsToType(interfaceToImplementationsMap.get(typeName));
        if (baseClassToImplementationsMap.containsKey(typeName))
            return mapImplementationsToType(baseClassToImplementationsMap.get(typeName));
        return Collections.emptyList();
    }

    private static <T> List<Class<T>> mapImplementationsToType(List<Class<?>> classes) {
        List<Class<T>> result = new ArrayList<>();
        for (Class<?> class_ : classes) {
            //noinspection unchecked
            result.add((Class<T>) class_);
        }
        return result;
    }
}
