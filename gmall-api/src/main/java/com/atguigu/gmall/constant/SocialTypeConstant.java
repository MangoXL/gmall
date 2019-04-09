package com.atguigu.gmall.constant;

public enum SocialTypeConstant {
    QQ("1","qq"),
    WEIBO("2","weibo");
    private String id;
    private String type;

    SocialTypeConstant(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
}
