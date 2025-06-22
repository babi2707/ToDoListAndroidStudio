package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // database
        private const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 3

        // registers
        private const val TABLE_REGISTERS = "registers"
        private const val KEY_ID_USER = "idUser"
        private const val KEY_NAME = "name"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_PASSWORD_IV = "passwordIv"

        // tasks
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
        val createTableRegisters = ("CREATE TABLE $TABLE_REGISTERS("
                + "$KEY_ID_USER INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_NAME TEXT,"
                + "$KEY_USERNAME TEXT,"
                + "$KEY_EMAIL TEXT UNIQUE,"
                + "$KEY_PASSWORD TEXT,"
                + "$KEY_PASSWORD_IV TEXT)")
        db.execSQL(createTableRegisters)

        val createTable = ("CREATE TABLE $TABLE_TASKS("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_ID_USER INTEGER,"
                + "$KEY_DATE TEXT,"
                + "$KEY_TASK TEXT,"
                + "$KEY_IS_DONE INTEGER,"
                + "$KEY_IS_ENCRYPTED INTEGER,"
                + "$KEY_DATE_IV TEXT,"
                + "$KEY_TASK_IV TEXT,"
                + "FOREIGN KEY($KEY_ID_USER) REFERENCES $TABLE_REGISTERS($KEY_ID_USER) ON DELETE CASCADE)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    // ********** registers **********
    fun addUser(register: RegisterItem): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, register.name)
            put(KEY_USERNAME, register.username)
            put(KEY_EMAIL, register.email)
            put(KEY_PASSWORD, register.password)
            put(KEY_PASSWORD_IV, register.passwordIv)
        }
        val id = db.insert(TABLE_REGISTERS, null, values)
        db.close()
        return id
    }

    fun checkUser(register: RegisterItem): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $KEY_ID_USER FROM $TABLE_REGISTERS WHERE $KEY_USERNAME = ? OR $KEY_EMAIL = ?",
            arrayOf(register.username, register.email)
        )
        val exists = cursor.count > 0

        cursor.close()
        db.close()
        return exists
    }

    fun getUserByUsername(username: String): RegisterItem? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REGISTERS WHERE $KEY_USERNAME = ?",
            arrayOf(username)
        )
        var user: RegisterItem? = null
        if (cursor.moveToFirst()) {
            user = RegisterItem(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_USER)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)),
                passwordIv = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD_IV))
            )
        }

        cursor.close()
        db.close()
        return user
    }

    // ********** tasks **********
    fun addTask(task: TaskItem): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_ID_USER, task.idUser)
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

    fun getAllTasksByUserId(id: Long): List<TaskItem> {
        val taskList = mutableListOf<TaskItem>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $KEY_ID_USER = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val task = TaskItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                    idUser = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_USER)),
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

    fun getAllTasks(): List<TaskItem> {
        val taskList = mutableListOf<TaskItem>()
        val selectQuery = "SELECT * FROM $TABLE_TASKS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val task = TaskItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                    idUser = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_USER)),
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

    fun getTaskById(id: Long): TaskItem? {
        val selectQuery = "SELECT * FROM $TABLE_TASKS WHERE $KEY_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(id.toString()))
        var task: TaskItem? = null

        if (cursor.moveToFirst()) {
            task = TaskItem(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                task = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK)),
                isDone = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_DONE)) == 1,
                isEncrypted = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ENCRYPTED)) == 1,
                dateIv = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_IV)),
                taskIv = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_IV))
            )
        }
        cursor.close()
        db.close()
        return task
    }

    fun updateTask(task: TaskItem): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DATE, task.date)
            put(KEY_TASK, task.task)
            put(KEY_IS_DONE, if (task.isDone) 1 else 0)
            put(KEY_IS_ENCRYPTED, if (task.isEncrypted) 1 else 0)
            put(KEY_DATE_IV, task.dateIv)
            put(KEY_TASK_IV, task.taskIv)
        }

        val rowsAffected = db.update(
            TABLE_TASKS,
            values,
            "$KEY_ID = ?",
            arrayOf(task.id.toString())
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