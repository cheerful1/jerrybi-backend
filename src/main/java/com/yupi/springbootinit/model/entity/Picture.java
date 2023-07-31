package com.yupi.springbootinit.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : wangshanjie
 * @date : 19:33 2023/6/28
 */
@Data
public class Picture implements Serializable {
    private String title;

    private String url;

    private static final long serialVersionUID = 1L;

}
