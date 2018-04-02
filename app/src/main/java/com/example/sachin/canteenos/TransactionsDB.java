package com.example.sachin.canteenos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

/**
 * Created by sachin on 28/3/18.
 */

public class TransactionsDB extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Transactions.db";
    private static final String TABLE_NAME = "transactions";
    private static final String COLUMN_1 = "rollno";
    private static final String COLUMN_2 = "amount";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_1 + " TEXT," +
                    COLUMN_2 + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    @SuppressWarnings("WeakerAccess")
    public TransactionsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database.
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Inserts an entry into the database.
     * @param rollno, amount : Entry elements.
     * @return True if inserted entry if insertion succeeds; False otherwise.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean insertData(SQLiteDatabase db, String rollno, String amount) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_1, rollno);
        values.put(COLUMN_2, amount);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
        return (newRowId == -1); // return True if insertion is successful, False otherwise.
    }

    /**
     * Gets all entries.
     * @return res All the entries as a Cursor object.
     */
    @SuppressWarnings("WeakerAccess")
    public Cursor getAllData(SQLiteDatabase db) {
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return res;
    }

    /**
     * Updates an entry from database.
     * @param id ID of entry to be updated.
     * @return ID of updated entry if it exists; -1 otherwise.
     */
    @SuppressWarnings("WeakerAccess")
    public Integer updateData(SQLiteDatabase db, String id, String rollno, String amount) {
        ContentValues values = new ContentValues();
        values.put(_ID, id);
        values.put(COLUMN_1, rollno);
        values.put(COLUMN_2, amount);

        // Update the given row, returning the primary key value of the updated row
        return db.update(TABLE_NAME, values, "_ID = ?", new String[] {id});
    }

    /**
     * Deletes an entry from database.
     * @param id ID of entry to be deleted.
     * @return ID of deleted entry if it exists; -1 otherwise.
     */
    @SuppressWarnings("WeakerAccess")
    public Integer deleteData(SQLiteDatabase db, String id) {
        return db.delete(TABLE_NAME, "_ID = ?", new String[] {id});
    }
}
