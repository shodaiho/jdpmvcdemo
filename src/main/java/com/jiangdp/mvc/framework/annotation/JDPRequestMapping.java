package com.jiangdp.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * JDPRequestMapping
 * <p>
 * Created by morningrain on 2019/6/21.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JDPRequestMapping {
    String value() default "";

}
