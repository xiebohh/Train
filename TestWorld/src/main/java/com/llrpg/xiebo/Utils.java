package com.llrpg.xiebo;

/**
 * Created by Administrator on 2016/12/28.
 */
public class Utils {
    public static void getPureClassName(Class clazz) {
        String name = clazz.getName();
        String simpleName = clazz.getSimpleName();
        System.out.println(name);
        System.out.println(simpleName);

    }
}
