package com.zdl.swipdelete;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class MyBean {
    private String name;

    public MyBean(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
