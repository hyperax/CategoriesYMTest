package ru.yandex.money.categories.network.api.yandex.model;

import java.util.List;

public class CategoryNetworkModel {

    private String title;

    private long id;

    private List<CategoryNetworkModel> subs;

    public long getId() {
        return id;
    }

    public CategoryNetworkModel setId(long id) {
        this.id = id;
        return this;
    }

    public List<CategoryNetworkModel> getSubs() {
        return subs;
    }

    public CategoryNetworkModel setSubs(List<CategoryNetworkModel> subs) {
        this.subs = subs;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CategoryNetworkModel setTitle(String title) {
        this.title = title;
        return this;
    }
}
