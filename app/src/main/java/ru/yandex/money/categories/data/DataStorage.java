package ru.yandex.money.categories.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DataStorage {

    AppSqliteOpenHelper openHelper;
    SQLiteDatabase database;
    DatabaseCompartment dataCompartment;

    private static DataStorage instance;
    private static final Object INIT_LOCK = new Object();

    private DataStorage(Context context) {
        openHelper = new AppSqliteOpenHelper(context);
        database = openHelper.getWritableDatabase();
        dataCompartment = cupboard().withDatabase(database);
    }

    public static DataStorage get(Context context) {
        if (instance == null) {
            synchronized (INIT_LOCK) {
                if (instance == null) {
                    instance = new DataStorage(context);
                }
            }
        }
        return instance;
    }

    public long put(Object entity) {
        return dataCompartment.put(entity);
    }

    public void put(Collection<?> entities) {
        dataCompartment.put(entities);
    }

    public int clearTable(Class classId) {
        return dataCompartment.delete(classId, null);
    }

    public boolean delete(Object entity) {
        return dataCompartment.delete(entity);
    }

    public boolean delete(Class<?> className, long id) {
        return dataCompartment.delete(className, id);
    }

    public int delete(Class<?> className, String selection, String... args) {
        return dataCompartment.delete(className, selection, args);
    }

    public <T> List<T> get(Class<T> className) {
        return dataCompartment.query(className).list();
    }

    public <T> T get(Class<T> className, long id) {
        return dataCompartment.get(className, id);
    }

    public <T> List<T> getWithSelectionList(Class<T> className, String selection, String... args) {
        return dataCompartment.query(className).withSelection(selection, args).list();
    }

    public <T> T getWithSelectionObject(Class<T> className, String selection, String... args) {
        return dataCompartment.query(className).withSelection(selection, args).get();
    }

    public <T> DatabaseCompartment.QueryBuilder<T> getQuery(Class<T> className) {
        return dataCompartment.query(className);
    }

    public int update(java.lang.Class<?> entityClass, android.content.ContentValues values,
                      java.lang.String selection, java.lang.String... selectionArgs) {
        return dataCompartment.update(entityClass, values, selection, selectionArgs);
    }

    public void beginTransaction() {
        database.beginTransactionNonExclusive();
    }

    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    public void endTransaction() {
        database.endTransaction();
    }

}
