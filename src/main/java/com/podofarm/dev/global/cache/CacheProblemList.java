package com.podofarm.dev.global.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented

//나중에 캐시를 개별적으로 관리하고 싶다면, config의 내용을 옮길 것.
public @interface CacheProblemList {
    String cacheName() default "Cache-problemList";
}
