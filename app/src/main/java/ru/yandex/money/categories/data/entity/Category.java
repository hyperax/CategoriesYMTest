package ru.yandex.money.categories.data.entity;

public class Category {

    private Long _id;

    private long outerId;

    private String title;

    private long parentId;

    public Long getId() {
        return _id;
    }

    public Category setId(Long id) {
        this._id = id;
        return this;
    }

    public long getParentId() {
        return parentId;
    }

    public Category setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Category setTitle(String title) {
        this.title = title;
        return this;
    }

    public long getOuterId() {
        return outerId;
    }

    public Category setOuterId(long outerId) {
        this.outerId = outerId;
        return this;
    }
}
