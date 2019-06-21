package com.jiangdp.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * JDPRequestParam
 * <p>
 * Created by morningrain on 2019/6/21.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JDPRequestParam {
    String value() default "";
}
