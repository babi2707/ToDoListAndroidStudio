package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "tasks"
        private const val KEY_ID = "id"
        private const val KEY_DATE = "date"
        private const val KEY_TASK = "task"
        private const val KEY_IS_DONE = "is_done"
        private const val KEY_IS_ENCRYPTED = "is_encrypted"
        private const val KEY_DATE_IV = "dateIv"
        private const val KEY_TASK_IV = "taskIv"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_TASKS("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_DATE TEXT,"
                + "$KEY_TASK TEXT,"
                + "$KEY_IS_DONE INTEGER,"
                + "$KEY_IS_ENCRYPTED INTEGER,"
                + "$KEY_DATE_IV TEXT,"
                + "$KEY_TASK_IV TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun addTask(task: TaskItem): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DATE, task.date)
            put(KEY_TASK, task.task)
            put(KEY_IS_DONE, if (task.isDone) 1 else 0)
            put(KEY_IS_ENCRYPTED, if (task.isEncrypted) 1 else 0)
            put(KEY_DATE_IV, task.dateIv)
            put(KEY_TASK_IV, task.taskIv)
        }
        val id = db.insert(TABLE_TASKS, null, values)
        db.close()
        return id
    }

    fun getAllTasks(): List<TaskItem> {
        val taskList = mutableListOf<TaskItem>()
        val selectQuery = "SELECT * FROM $TABLE_TASKS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val task = TaskItem(
                    date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                    task = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK)),
                    isDone = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_DONE)) == 1,
                    isEncrypted = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ENCRYPTED)) == 1,
                    dateIv = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_IV)),
                    taskIv = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_IV))
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return taskList
    }

    fun updateTask(oldTask: TaskItem, newTask: TaskItem): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DATE, newTask.date)
            put(KEY_TASK, newTask.task)
            put(KEY_IS_DONE, if (newTask.isDone) 1 else 0)
            put(KEY_IS_ENCRYPTED, if (newTask.isEncrypted) 1 else 0)
            put(KEY_DATE_IV, newTask.dateIv)
            put(KEY_TASK_IV, newTask.taskIv)
        }

        val rowsAffected = db.update(
            TABLE_TASKS,
            values,
            "$KEY_DATE = ? AND $KEY_TASK = ?",
            arrayOf(oldTask.date, oldTask.task)
        )
        db.close()
        return rowsAffected
    }

    fun deleteTask(task: TaskItem): Int {
        val db = this.writableDatabase
        val rowsDeleted = db.delete(
            TABLE_TASKS,
            "$KEY_DATE = ? AND $KEY_TASK = ?",
            arrayOf(task.date, task.task)
        )
        db.close()
        return rowsDeleted
    }

    fun deleteAllTasks() {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, null, null)
        db.close()
    }
}