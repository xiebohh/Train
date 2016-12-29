package com.llrpg.xiebo;

/**
 * Created by Administrator on 2016/12/28.
 */

public class People {
    public static class Inner {
        private static final String CLASS_NAME = new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                System.out.println(clazzName);
                return clazzName.substring(clazzName.lastIndexOf('.') + 1,
                        clazzName.lastIndexOf('$'));
            }
        }.getClassName();

    }
    public static void main(String[] args) throws Exception {
        System.out.println(Inner.CLASS_NAME);
        System.out.println(Inner.class.getSimpleName());
    }
}
