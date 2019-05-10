package com.equiron.acc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsView {
    String map();

    String reduce() default "";

    public final String COUNT = "_count";
    public final String SUM   = "_sum";
    public final String STATS = "_stats";
}
