 package com.devivan.economicsupervision.SQLite;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class SQLiteUtil {
    // SqLite structures [ create ]:
    public static String CREATE_TABLE_ACCOUNTS = "CREATE TABLE accounts" +
            "(id TEXT PRIMARY KEY,"+
            "currency TEXT,"+
            "money REAL)";
    public static String CREATE_TABLE_CONCEPTS = "CREATE TABLE concepts" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "accountId TEXT,"+
            "name TEXT,"+
            "FOREIGN KEY(accountId) REFERENCES accounts(id) " +
            "ON DELETE CASCADE)";
    public static String CREATE_TABLE_MOVEMENTS = "CREATE TABLE movements" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "accountId TEXT,"+
            "conceptId INTEGER,"+
            "date TEXT," +
            "location TEXT,"+
            "subCategoryId TEXT,"+
            "type TEXT,"+
            "value REAL,"+
            "FOREIGN KEY(accountId) REFERENCES accounts(id) " +
            "ON DELETE CASCADE)";

    // SqLite structures [ drop ]:
    public static String DROP_TABLE_ACCOUNTS = "DROP TABLE accounts";
    public static String DROP_TABLE_CONCEPTS = "DROP TABLE concepts";
    public static String DROP_TABLE_MOVEMENTS = "DROP TABLE movements";
}
