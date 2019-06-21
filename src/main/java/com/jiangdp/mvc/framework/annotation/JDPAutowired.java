package com.jiangdp.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * JDPAutowired
 * <p>
 * Created by morningrain on 2019/6/21.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JDPAutowired {
}
