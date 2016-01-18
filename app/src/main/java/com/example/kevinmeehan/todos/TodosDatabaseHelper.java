package com.example.kevinmeehan.todos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinmeehan on 1/17/16.
 */
public class TodosDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite.Todos";

    private static final String DATABASE_NAME = "todosDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TODOS = "todos";

    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_ISDELETED = "isDeleted";
    private static final String KEY_TODO_ISDONE = "isDone";
    private static final String KEY_TODO_ORDER = "sortOrder";

    private static TodosDatabaseHelper sInstance;



    public static synchronized TodosDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodosDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @SuppressLint("NewApi") // for the sake of the demo ...
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY, " + // Define a primary key
                KEY_TODO_TEXT + " TEXT, " +
                KEY_TODO_ISDELETED + " BOOLEAN DEFAULT 0, " +
                KEY_TODO_ISDONE + " BOOLEAN DEFAULT 0, " +
                KEY_TODO_ORDER + " INTEGER" +
                ")";

        db.execSQL(CREATE_TODOS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            onCreate(db);
        }
    }

    // Insert a todo into the database
    public void addTodo(Todo todo) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, todo.getText());
            values.put(KEY_TODO_ISDELETED, todo.getIsDeleted() ? 1 : 0);
            values.put(KEY_TODO_ISDONE, todo.getIsDone() ? 1 : 0);
            values.put(KEY_TODO_ORDER, todo.getOrder());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TODOS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add todo to database");
        } finally {
            db.endTransaction();
        }
    }

    public void updateTodo(Todo todo) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, todo.getText());
            values.put(KEY_TODO_ISDELETED, todo.getIsDeleted());
            values.put(KEY_TODO_ISDONE, todo.getIsDone());
            values.put(KEY_TODO_ORDER, todo.getOrder());

            // 3. updating row
            int i = db.update(TABLE_TODOS, //table
                    values, // column/value
                    KEY_TODO_ID+" = ?", // selections
                    new String[] { String.valueOf(todo.getId()) }); //select

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TODOS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add todo to database");
        } finally {
            db.endTransaction();
        }
    }

    // Get all un-deleted todos in the database
    public ArrayList<Todo> getAllVisibleTodos() {
        ArrayList<Todo> todos = new ArrayList<Todo>();

        // SELECT * FROM TODOS
        String TODOS_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = %s ORDER BY %s ASC",
                        TABLE_TODOS,
                        KEY_TODO_ISDELETED,
                        0,
                        KEY_TODO_ORDER);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.setText(cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)));
                    newTodo.setIsDeleted(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ISDELETED)));
                    newTodo.setIsDone(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ISDONE)));
                    todos.add(newTodo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todos from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos;
    }

    // Get all todos in the database
    public ArrayList<Todo> getAllTodos() {
        ArrayList<Todo> todos = new ArrayList<Todo>();

        // SELECT * FROM TODOS
        String TODOS_SELECT_QUERY =
                String.format("SELECT * FROM %s ORDER BY %s ASC",
                        TABLE_TODOS,
                        KEY_TODO_ORDER);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.setText(cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)));
                    newTodo.setIsDeleted(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ISDELETED)));
                    newTodo.setIsDone(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ISDONE)));
                    todos.add(newTodo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todos from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos;
    }

    // Delete all todos and users in the database
    public void deleteAllTodos() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_TODOS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all todos");
        } finally {
            db.endTransaction();
        }
    }
}
