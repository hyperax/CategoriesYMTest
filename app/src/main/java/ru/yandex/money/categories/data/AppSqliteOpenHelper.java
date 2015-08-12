package ru.yandex.money.categories.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.yandex.money.categories.data.entity.Category;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class AppSqliteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_FILE = "application.db";
    private static final int DB_VERSION = 1;

    static {
        cupboard().register(Category.class);
    }

    public AppSqliteOpenHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}