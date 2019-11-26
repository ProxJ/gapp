package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

import java.io.File
import java.text.SimpleDateFormat


class DatabaseHelper(p: Context) : SQLiteOpenHelper(p, "$DB_NAME.db", null, DATABASE_VERSION) {

    // The Android's default system path of your application database.

    private val DB_PATH: String? = null
    private val createTableDays =
        "CREATE TABLE IF NOT EXISTS $TABLE_DAYS($DATE datetime primary key, $TIME TIME)"
    private val createTableExercises =
        "CREATE TABLE IF NOT EXISTS $TABLE_EXERCISE($EId INTEGER PRIMARY KEY, $MGroup varchar(20), $MType varchar(20), $EType varchar(20), $WEIGHT varchar(10), $REPS varchar(10), $RTime TIME, $COMMENT varchar(255), $DATE DATE , $TIME TIME, $SUBSET BOOL, FOREIGN KEY($DATE) REFERENCES $TABLE_DAYS($DATE) ON DELETE CASCADE)"

    private val createTableSubsets =
        "CREATE TABLE IF NOT EXISTS $TABLE_SUBSETS($TIME TIME not null references $TABLE_EXERCISE($TIME) ON DELETE CASCADE, $WEIGHT varchar(10) not null, $REPS varchar(10) not null, $RTime TIME, $DESCRIPTION varchar(20), primary key ($TIME, $WEIGHT, $REPS))"

    private val createTableDaysFTS =
        "CREATE VIRTUAL TABLE IF NOT EXISTS $TABLE_DAYS USING fts3($DATE, $TIME)"
    private val createTableExercisesFTS =
        "CREATE VIRTUAL TABLE IF NOT EXISTS $TABLE_EXERCISE USING fts3($MGroup, $MType, $EType, $WEIGHT, $REPS, $RTime, $COMMENT, $DATE, $TIME)"

    private val createTableSubsetsFTS =
        "CREATE VIRTUAL TABLE IF NOT EXISTS $TABLE_SUBSETS USING fts3($TIME, $WEIGHT, $REPS, $RTime, $DESCRIPTION)"


    private val db: SQLiteDatabase
        get() = writableDatabase

    fun doUpdate(sql: String, params: Array<String>) {
        try {
            return writableDatabase.execSQL(sql, params)
        } catch (mSQLException: SQLException) {
            System.err.println("-- doUpdate --\n$sql")
            mSQLException.printStackTrace(System.err)
        }

    }

    fun doInsert(table: String, columns: Array<String>, values: Array<String>): Long {
        val params = ContentValues()
        for (i in columns.indices) params.put(columns[i], values[i])
        return db.insert(table, null, params)
    }

    fun doQueryMatch(
        fts_table: String,
        field: String, query: String,
        columns: Array<String>?,
        group: String?,
        having: String?,
        sort_order: String?): Cursor? {
        val selection = "$field MATCH ?"
        val selectionArgs = arrayOf("$query*")

        return SQLiteQueryBuilder().run {
            tables = fts_table
            query(readableDatabase,
                columns, selection, selectionArgs, group, having, sort_order)
        }

    }

    fun doQuery(sql: String): Cursor? {
        return try {
            readableDatabase.rawQuery(sql, null)
        } catch (mSQLException: SQLException) {
            System.err.println("-- doQuery --\n$sql")
            mSQLException.printStackTrace()
            null
        }
    }

    fun updateDetails(table: String, values: ContentValues, field: String, identifier: String) {
        try {
            writableDatabase.update(table, values, "$field = $identifier", null)
        } catch (m: SQLException) {
            System.err.println("-- updateDetails --\n")
            m.printStackTrace(System.err)
        }

    }

    fun deleteEntry(table: String, key: String,  value: String): Boolean {
        return db.delete(table, "$key = $value", null) > 0
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(createTableDays)
        db.execSQL(createTableExercises)
        db.execSQL(createTableSubsets)

        db.execSQL(createTableDaysFTS)
        db.execSQL(createTableExercisesFTS)
        db.execSQL(createTableSubsetsFTS)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DAYS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBSETS")

        db.execSQL("DROP TABLE IF EXISTS $TABLE_DAYS_FTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE_FTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBSETS_FTS")

        onCreate(db)
    }

    companion object {
        const val DB_NAME = "my_gains"
        const val DATABASE_VERSION = 1

        //table names
        internal const val TABLE_DAYS = "days"
        internal const val TABLE_EXERCISE = "exercises"
        internal const val TABLE_SUBSETS = "subsets"
        internal const val TABLE_DAYS_FTS = "days"
        internal const val TABLE_EXERCISE_FTS = "exercises"
        internal const val TABLE_SUBSETS_FTS = "subsets"

        //column names
        const val MGroup = "muscle_group"
        const val MType = "muscle_type"
        const val EType = "exercise_type"
        const val EId = "exercise_id"
        const val REPS = "number_of_reps"
        const val WEIGHT = "weight"
        const val COMMENT = "comment"
        const val RTime = "rest_time"
        const val TIME = "time"
        const val DATE = "date"
        const val SUBSET = "subset"
        const val DESCRIPTION = "description"
    }
}
