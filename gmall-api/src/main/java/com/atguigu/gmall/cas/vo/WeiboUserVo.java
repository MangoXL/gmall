package com.atguigu.gmall.cas.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeiboUserVo implements Serializable{
    private Long id;
    private String name;
    private String province;
    private String location;
    private String profile_image_url;
}
