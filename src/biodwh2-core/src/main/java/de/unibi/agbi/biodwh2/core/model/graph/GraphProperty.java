package de.unibi.agbi.biodwh2.core.model.graph;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GraphProperty {
    String value() default "";

    boolean ignoreEmpty() default false;

    String[] emptyPlaceholder() default {};

    ValueTransformation transformation() default ValueTransformation.NONE;
}
