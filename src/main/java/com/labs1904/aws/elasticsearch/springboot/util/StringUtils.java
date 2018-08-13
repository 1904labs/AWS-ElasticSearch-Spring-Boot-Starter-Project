package com.labs1904.aws.elasticsearch.springboot.util;

public class StringUtils {
    public static boolean checkNullOrEmpty(String string){
        return string != null && !string.isEmpty();
    }
}
