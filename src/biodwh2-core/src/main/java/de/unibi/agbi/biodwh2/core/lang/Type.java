package de.unibi.agbi.biodwh2.core.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Type implements Serializable {
    private static final long serialVersionUID = -7123224065146725199L;

    private Class<?> type;
    private Class<?> componentType;
    private boolean isList;

    public Type(final Class<?> type) {
        this.type = type;
        if (type.isArray()) {
            isList = true;
            this.componentType = type.getComponentType();
        }
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getComponentType() {
        return componentType;
    }

    public boolean isList() {
        return isList;
    }

    @SuppressWarnings("rawtypes")
    public static Type fromObject(final Object obj) {
        if (obj == null)
            return null;
        final Type result = new Type(obj.getClass());
        if (Collection.class.isAssignableFrom(result.type)) {
            result.isList = true;
            final Set<Class<?>> componentTypeCandidates = new HashSet<>();
            final Collection collection = (Collection) obj;
            for (final Object element : collection) {
                if (element != null)
                    componentTypeCandidates.add(element.getClass());
            }
            if (componentTypeCandidates.size() > 0) {
                Class<?> commonBaseClass = null;
                for (final Class<?> candidate : componentTypeCandidates) {
                    if (commonBaseClass == null || candidate.isAssignableFrom(commonBaseClass))
                        commonBaseClass = candidate;
                    else {
                        // Find the next common parent class which in the worst case would be Object.class
                        Class<?> parentClass = candidate;
                        while ((parentClass = parentClass.getSuperclass()) != null) {
                            if (parentClass.isAssignableFrom(commonBaseClass)) {
                                commonBaseClass = parentClass;
                                break;
                            }
                        }
                    }
                }
                result.componentType = commonBaseClass;
            }
        }
        return result;
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(type);
        out.writeObject(componentType);
        out.writeBoolean(isList);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        type = (Class<?>) in.readObject();
        componentType = (Class<?>) in.readObject();
        isList = in.readBoolean();
    }
}
