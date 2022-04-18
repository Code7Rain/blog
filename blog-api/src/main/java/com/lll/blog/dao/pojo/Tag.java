package com.lll.blog.dao.pojo;

public class Tag {

    private Long id;

    private String avatar;

    private String tagName;

    public Tag() {
    }

    public Tag(Long id, String avatar, String tagName) {
        this.id = id;
        this.avatar = avatar;
        this.tagName = tagName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}