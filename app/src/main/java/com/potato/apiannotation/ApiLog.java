package com.potato.apiannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Potato
 * api注解（辅助打印信息）
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface ApiLog {
    /**
     * 英文名字
     */
    String nameEnglish();

    /**
     * 中文名字
     */
    String nameChinese() default "";
}
