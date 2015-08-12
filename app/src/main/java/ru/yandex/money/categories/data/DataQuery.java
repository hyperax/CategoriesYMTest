package ru.yandex.money.categories.data;

import android.content.Context;

import java.util.List;

import ru.yandex.money.categories.data.entity.Category;

public class DataQuery {

    private static final String ID = "_id";

    private static DataQuery instance;

    private static final Object INIT_LOCK = new Object();

    private DataStorage dataStorage;

    private DataQuery(Context context) {
        dataStorage = DataStorage.get(context.getApplicationContext());
    }

    public static DataQuery get(Context context) {
        if (instance == null) {
            synchronized (INIT_LOCK) {
                if (instance == null) {
                    instance = new DataQuery(context);
                }
            }
        }
        return instance;
    }


    public void clearCategories() {
        dataStorage.clearTable(Category.class);
    }

    public void putCategories(List<Category> categories) {
        dataStorage.put(categories);
    }

    public List<Category> getCategories(long parentId) {
        return dataStorage.getQuery(Category.class)
                .withSelection("parentId=" + parentId)
                .list();
    }

    public boolean hasChildCategories(long id) {
        return dataStorage.getQuery(Category.class)
                .withProjection(ID)
                .withSelection("parentId=" + id)
                .list().size() > 0;
    }

    public void beginTransaction() {
        dataStorage.beginTransaction();
    }

    public void endTransaction() {
        dataStorage.endTransaction();
    }

    public long putCategory(Category category) {
        return dataStorage.put(category);
    }

    public void setTransactionSuccessful() {
        dataStorage.setTransactionSuccessful();
    }

    public Category getCategory(long id) {
        return dataStorage.get(Category.class, id);
    }
}
