package de.unibi.agbi.biodwh2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

public final class Factory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
    private static final List<String> IGNORED_MODULE_PREFIXES = Arrays.asList("java.", "jdk.", "javafx.", "oracle.",
                                                                              "kotlin.", "logback.", "com.fasterxml.",
                                                                              "slf4j.");
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

    private void collectAllClassPaths() {
        for (final ModuleReference m : collectAllModules()) {
            try (final ModuleReader moduleReader = m.open()) {
                moduleReader.list().filter(Factory::isUriClassInBioDWH).map(Factory::getClassPathFromUri).forEach(
                        allClassPaths::add);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private Set<ModuleReference> collectAllModules() {
        final Class<?>[] callStack = getCallStack();
        final Set<ModuleLayer> layers = new HashSet<>();
        for (final Class<?> c : callStack)
            collectLayersRecursive(c.getModule().getLayer(), layers);
        final Set<ModuleReference> modules = new HashSet<>();
        for (final ModuleLayer layer : layers)
            for (final ResolvedModule module : layer.configuration().modules())
                if (isModuleNotIgnored(module))
                    modules.add(module.reference());
        return modules;
    }

    private Class<?>[] getCallStack() {
        Class<?>[] result = getCallStackFromStackWalker();
        if (result == null)
            result = getCallStackFromSecurityManager();
        if (result == null)
            result = getCallStackFromException();
        return result.length == 0 ? new Class<?>[]{getClass()} : result;
    }

    private Class<?>[] getCallStackFromStackWalker() {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        final PrivilegedAction<Class<?>[]> stackWalkerAction = () -> walker.walk(
                s -> s.map(StackWalker.StackFrame::getDeclaringClass).toArray(Class[]::new));
        try {
            return AccessController.doPrivileged(stackWalkerAction);
        } catch (Exception ignored) {
        }
        try {
            return stackWalkerAction.run();
        } catch (Exception ignored) {
        }
        return null;
    }

    private Class<?>[] getCallStackFromSecurityManager() {
        PrivilegedAction<Class<?>[]> callerResolverAction = () -> new SecurityManager() {
            @Override
            public Class<?>[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        try {
            return AccessController.doPrivileged(callerResolverAction);
        } catch (Exception ignored) {
        }
        try {
            return callerResolverAction.run();
        } catch (Exception ignored) {
        }
        return null;
    }

    private Class<?>[] getCallStackFromException() {
        try {
            throw new Exception();
        } catch (final Exception e) {
            final List<Class<?>> classes = new ArrayList<>();
            for (final StackTraceElement elt : e.getStackTrace()) {
                try {
                    classes.add(Class.forName(elt.getClassName()));
                } catch (final Throwable ignored) {
                }
            }
            return classes.toArray(new Class<?>[0]);
        }
    }

    private void collectLayersRecursive(final ModuleLayer layer, final Set<ModuleLayer> layers) {
        if (layers.add(layer))
            for (final ModuleLayer parent : layer.parents())
                collectLayersRecursive(parent, layers);
    }

    private static boolean isModuleNotIgnored(final ResolvedModule module) {
        final String name = module.reference().descriptor().name();
        if (name == null)
            return true;
        return IGNORED_MODULE_PREFIXES.stream().noneMatch(name::startsWith);
    }

    private static boolean isUriClassInBioDWH(final String uri) {
        return uri.endsWith(".class") && uri.contains("de/unibi/agbi/biodwh2");
    }

    private static String getClassPathFromUri(final String uri) {
        return uri.replace("/", ".").replace(".class", "");
    }

    private void loadAllClasses() {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        for (final String classPath : allClassPaths)
            loadClassPath(classLoader, classPath);
    }

    public static synchronized Factory getInstance() {
        if (instance == null)
            instance = new Factory();
        return instance;
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
