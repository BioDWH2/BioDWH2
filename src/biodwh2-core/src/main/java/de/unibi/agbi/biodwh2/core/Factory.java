package de.unibi.agbi.biodwh2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Factory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
    private static final List<String> IGNORED_JARS = Arrays.asList("rt.jar", "idea_rt.jar", "aws-java-sdk-ec2",
                                                                   "proto-", "google-cloud-", "google-api-",
                                                                   "openstack4j-core", "selenium-", "google-api-client",
                                                                   "jackson-", "guava", "jetty", "netty-", "junit-",
                                                                   "com.intellij.rt");
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

    public static synchronized Factory getInstance() {
        if (instance == null)
            instance = new Factory();
        return instance;
    }

    private void loadAllClasses() {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        for (final String classPath : allClassPaths)
            loadClassPath(classLoader, classPath);
    }

    private void collectAllClassPaths() {
        final String runtimeClassPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        for (final String classPath : runtimeClassPath.split(File.pathSeparator)) {
            final File file = new File(classPath);
            if (file.isDirectory())
                iterateFileSystem(file, file.toURI().toString());
            else if (isValidJarFile(file))
                iterateJarFile(file);
        }
    }

    private static boolean isValidJarFile(final File file) {
        final String fileName = file.getName().toLowerCase(Locale.US);
        return file.isFile() && fileName.endsWith(".jar") && IGNORED_JARS.stream().noneMatch(fileName::contains);
    }

    private void iterateFileSystem(final File directory, final String rootPath) {
        final File[] files = directory.listFiles();
        if (files != null)
            iterateFiles(files, rootPath);
    }

    private void iterateFiles(final File[] files, final String rootPath) {
        for (final File file : files)
            if (file.isDirectory())
                iterateFileSystem(file, rootPath);
            else if (file.isFile())
                addUriIfValidClassPath(file.toURI().toString().substring(rootPath.length()));
    }

    private void addUriIfValidClassPath(final String uri) {
        if (isUriClassInBioDWH(uri))
            allClassPaths.add(getClassPathFromUri(uri));
    }

    private void iterateJarFile(final File file) {
        final Enumeration<JarEntry> entries = tryGetJarFileEntries(file);
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            if (!entry.isDirectory())
                addUriIfValidClassPath(entry.getName());
        }
    }

    private Enumeration<JarEntry> tryGetJarFileEntries(final File file) {
        try {
            return new JarFile(file).entries();
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to load JAR entries", e);
            return Collections.emptyEnumeration();
        }
    }

    private static boolean isUriClassInBioDWH(final String uri) {
        return uri.endsWith(".class") && uri.contains("de/unibi/agbi/biodwh2");
    }

    private static String getClassPathFromUri(final String uri) {
        return uri.replace("/", ".").replace(".class", "");
    }

    private void loadClassPath(final ClassLoader classLoader, final String classPath) {
        final Class<?> c = tryLoadClass(classLoader, classPath);
        if (c != null) {
            linkClassToParentInterfaces(c);
            linkClassToSuperclass(c);
        }
    }

    private static Class<?> tryLoadClass(final ClassLoader classLoader, final String classPath) {
        try {
            return classLoader.loadClass(classPath);
        } catch (ClassNotFoundException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to load class '" + classPath + "'", e);
            return null;
        }
    }

    private void linkClassToParentInterfaces(final Class<?> c) {
        for (final Class<?> classInterface : c.getInterfaces())
            linkClassToParentInterface(c, classInterface.getName());
    }

    private void linkClassToParentInterface(final Class<?> c, final String interfaceName) {
        if (!interfaceToImplementationsMap.containsKey(interfaceName))
            interfaceToImplementationsMap.put(interfaceName, new ArrayList<>());
        interfaceToImplementationsMap.get(interfaceName).add(c);
    }

    private void linkClassToSuperclass(final Class<?> c) {
        if (c.getSuperclass() != null) {
            final String superclassName = c.getSuperclass().getName();
            if (!baseClassToImplementationsMap.containsKey(superclassName))
                baseClassToImplementationsMap.put(superclassName, new ArrayList<>());
            baseClassToImplementationsMap.get(superclassName).add(c);
        }
    }

    public <T> List<Class<T>> getImplementations(final Class<T> type) {
        final String typeName = type.getName();
        if (interfaceToImplementationsMap.containsKey(typeName))
            return mapImplementationsToType(interfaceToImplementationsMap.get(typeName));
        if (baseClassToImplementationsMap.containsKey(typeName))
            return mapImplementationsToType(baseClassToImplementationsMap.get(typeName));
        return Collections.emptyList();
    }

    private static <T> List<Class<T>> mapImplementationsToType(final List<Class<?>> classes) {
        final List<Class<T>> result = new ArrayList<>();
        for (final Class<?> class_ : classes) {
            //noinspection unchecked
            result.add((Class<T>) class_);
        }
        return result;
    }
}
