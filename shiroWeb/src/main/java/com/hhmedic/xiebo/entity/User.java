package com.hhmedic.xiebo.entity;

import lombok.Getter;
import lombok.Setter;
/**
 * Created by i on 2016/10/2.
 */
@Getter
@Setter
public class User {
    private Integer id;
    private String userName;
    private String password;
    private Integer roleId;
}
