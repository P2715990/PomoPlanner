package com.example.pomoplanner.model

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase


class DBHelper(context: Context) : SQLiteOpenHelper(context, DataBaseName, null, ver) {

    // DB Config
    companion object {
        private val DataBaseName = "PomoPlannerDatabase.db"
        private val ver: Int = 1
    }

    // definition of table values
    object ProfileTableEntry {
        val ProfileTableName = "TProfile"
        val Column_ProfileId = "ProfileId"
        val Column_ProfileUsername = "ProfileUsername"
        val Column_ProfilePassword = "ProfilePassword"
        val Column_ProfileIsSelected = "ProfileSelected"
    }

    object TaskTableEntry {
        val TaskTableName = "TTask"
        val Column_TaskId = "TaskId"
        val Column_TaskDate = "TaskDate"
        val Column_TaskPriority = "TaskPriority"
        val Column_TaskIsCompleted = "TaskIsCompleted"
        val Column_TaskCategory = "TaskCategory"
        val Column_TaskDetails = "TaskDetails"
    }

    object SettingTableEntry {
        val SettingTableName = "TSetting"
        val Column_SettingDescription = "SettingDescription"
        val Column_SettingValue = "SettingValue"
    }

    // definition of table creation statements
    val sqlCreateTProfileStatement: String =
        "CREATE TABLE IF NOT EXISTS " + ProfileTableEntry.ProfileTableName + " (" +
                ProfileTableEntry.Column_ProfileId + " INTEGER NOT NULL UNIQUE, " +
                ProfileTableEntry.Column_ProfileUsername + " TEXT NOT NULL UNIQUE, " +
                ProfileTableEntry.Column_ProfilePassword + " TEXT, " +
                ProfileTableEntry.Column_ProfileIsSelected + " INTEGER NOT NULL DEFAULT 0, " +
                "PRIMARY KEY(" + ProfileTableEntry.Column_ProfileId + " AUTOINCREMENT))"

    val sqlCreateTTasksStatement: String =
        "CREATE TABLE IF NOT EXISTS " + TaskTableEntry.TaskTableName + " (" +
                TaskTableEntry.Column_TaskId + " INTEGER NOT NULL UNIQUE, " +
                ProfileTableEntry.Column_ProfileId + " INTEGER NOT NULL, " +
                TaskTableEntry.Column_TaskDate + " TEXT NOT NULL, " +
                TaskTableEntry.Column_TaskPriority + " TEXT NOT NULL DEFAULT 'Low', " +
                TaskTableEntry.Column_TaskIsCompleted + " INTEGER NOT NULL DEFAULT 0, " +
                TaskTableEntry.Column_TaskCategory + " TEXT, " +
                TaskTableEntry.Column_TaskDetails + " TEXT NOT NULL, " +
                "PRIMARY KEY(" + TaskTableEntry.Column_TaskId + " AUTOINCREMENT))"

    val sqlCreateTSettingStatement: String =
        "CREATE TABLE IF NOT EXISTS " + SettingTableEntry.SettingTableName + " (" +
                SettingTableEntry.Column_SettingDescription + " TEXT NOT NULL UNIQUE, " +
                SettingTableEntry.Column_SettingValue + " TEXT NOT NULL)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(sqlCreateTProfileStatement)
        db?.execSQL(sqlCreateTTasksStatement)
        db?.execSQL(sqlCreateTSettingStatement)

        if (isTableEmpty(SettingTableEntry.SettingTableName, db)) {
            assignDefaultSettings(db)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        TODO("Not yet required")
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    // function for checking if a table has any rows

    fun isTableEmpty(tableName: String, db: SQLiteDatabase?): Boolean {
        var flag = false
        val sqlStatement = "SELECT EXISTS(SELECT 1 FROM $tableName)"

        val cursor: Cursor? = db?.rawQuery(sqlStatement, null)
        cursor?.moveToFirst()

        if (cursor?.getInt(0) != 1) {
            flag = true
        }

        cursor?.close()
        return flag
    }

    // functions for TTask

    fun addTask(task: Task): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()

        cv.put(ProfileTableEntry.Column_ProfileId, task.profileId)
        cv.put(TaskTableEntry.Column_TaskDate, task.taskDate)
        cv.put(TaskTableEntry.Column_TaskPriority, task.taskPriority)
        cv.put(TaskTableEntry.Column_TaskIsCompleted, (if (task.taskIsCompleted) 1 else 0))
        cv.put(TaskTableEntry.Column_TaskCategory, (if (task.taskCategory == "") null else task.taskCategory))
        cv.put(TaskTableEntry.Column_TaskDetails, task.taskDetails)

        val success = db.insert(TaskTableEntry.TaskTableName, null, cv)
        db.close()
        return success != -1L
    }

    fun updateTask(task: Task): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()

        cv.put(ProfileTableEntry.Column_ProfileId, task.profileId)
        cv.put(TaskTableEntry.Column_TaskDate, task.taskDate)
        cv.put(TaskTableEntry.Column_TaskPriority, task.taskPriority)
        cv.put(TaskTableEntry.Column_TaskIsCompleted, (if (task.taskIsCompleted) 1 else 0))
        cv.put(TaskTableEntry.Column_TaskCategory, (if (task.taskCategory == "") null else task.taskCategory))
        cv.put(TaskTableEntry.Column_TaskDetails, task.taskDetails)

        val success = db.update(
            TaskTableEntry.TaskTableName,
            cv,
            "${TaskTableEntry.Column_TaskId} = ${task.taskId}",
            null
        ) == 1
        db.close()
        return success
    }

    fun deleteTask(task: Task): Boolean {
        val db: SQLiteDatabase = this.writableDatabase

        val success =
            db.delete(TaskTableEntry.TaskTableName, "${TaskTableEntry.Column_TaskId} = ${task.taskId}", null) == 1

        db.close()
        return success
    }

    fun getTask(taskId: Int): Task {
        val db: SQLiteDatabase = this.readableDatabase
        val sqlStatement =
            "SELECT * FROM ${TaskTableEntry.TaskTableName} WHERE ${TaskTableEntry.Column_TaskId} LIKE '$taskId'"

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        if (cursor.moveToFirst()) {
            val result = Task(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4) == 1,
                cursor.getString(5),
                cursor.getString(6)
            )
            db.close()
            cursor.close()
            return result
        } else {
            db.close()
            cursor.close()
            return Task(-1, -1, "", "", false, null, "")
        }
    }

    fun getTasks(profileId: Int, date: String, category: String, priority: String, status: Int): List<Task> {
        val db: SQLiteDatabase = this.readableDatabase
        var sqlStatement =
            "SELECT * FROM ${TaskTableEntry.TaskTableName} WHERE ${ProfileTableEntry.Column_ProfileId} LIKE '$profileId' AND  " +
                    "${TaskTableEntry.Column_TaskDate} LIKE '$date'"
        if (category != "All") {
            sqlStatement += " AND ${
                TaskTableEntry.Column_TaskCategory} LIKE '$category'"
        }
        if (priority != "All") {
            sqlStatement += " AND ${
                TaskTableEntry.Column_TaskPriority} LIKE '$priority'"
        }
        if (status != 2) {
            sqlStatement += " AND ${
                TaskTableEntry.Column_TaskIsCompleted} LIKE '$status'"
        }

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        val taskItems = mutableListOf<Task>()
        if (cursor.moveToFirst()) {
            do {
                val taskItem = Task(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 1,
                    cursor.getString(5),
                    cursor.getString(6)
                )
                taskItems += taskItem
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return taskItems
    }

    fun getTaskCategories(profileId: Int, date: String): List<String> {
        val db: SQLiteDatabase = this.readableDatabase
        val sqlStatement =
            "SELECT * FROM ${TaskTableEntry.TaskTableName} WHERE ${ProfileTableEntry.Column_ProfileId} LIKE '$profileId' AND  " +
                    "${TaskTableEntry.Column_TaskDate} LIKE '$date' AND " +
                    "${TaskTableEntry.Column_TaskCategory} IS NOT NULL"

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        val taskCategories = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(5)
                taskCategories += category
            } while (cursor.moveToNext())
        }

        db.close()
        cursor.close()
        return taskCategories.distinct()
    }

    // functions for TProfile

    fun addProfile(profile: Profile): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()

        cv.put(ProfileTableEntry.Column_ProfileUsername, profile.profileUsername)
        cv.put(ProfileTableEntry.Column_ProfilePassword, profile.profilePassword)

        val success = db.insert(ProfileTableEntry.ProfileTableName, null, cv)
        db.close()
        return success != -1L
    }

    fun updateProfile(profile: Profile): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()

        cv.put(ProfileTableEntry.Column_ProfileUsername, profile.profileUsername)
        cv.put(ProfileTableEntry.Column_ProfilePassword, profile.profilePassword)
        cv.put(ProfileTableEntry.Column_ProfileIsSelected, if (profile.profileIsSelected) 1 else 0)

        val success = db.update(
            ProfileTableEntry.ProfileTableName,
            cv,
            "${ProfileTableEntry.Column_ProfileId} = ${profile.profileId}",
            null
        ) == 1
        db.close()
        return success
    }

    fun deleteProfile(profile: Profile): Boolean {
        val db: SQLiteDatabase = this.writableDatabase

        val success =
            db.delete(ProfileTableEntry.ProfileTableName, "${ProfileTableEntry.Column_ProfileId} = ${profile.profileId}", null) == 1

        db.close()
        return success
    }

    fun getProfile(profileId: Int): Profile {
        val db: SQLiteDatabase = this.readableDatabase
        val sqlStatement =
            "SELECT * FROM ${ProfileTableEntry.ProfileTableName} WHERE ${ProfileTableEntry.Column_ProfileId} LIKE '$profileId'"

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        if (cursor.moveToFirst()) {
            val result = Profile(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) == 1,
            )
            db.close()
            cursor.close()
            return result
        } else {
            db.close()
            cursor.close()
            return Profile(-1, "", "", false)
        }
    }

    fun getProfile(profileUsername: String): Profile {
        val db: SQLiteDatabase = this.readableDatabase
        val sqlStatement =
            "SELECT * FROM ${ProfileTableEntry.ProfileTableName} WHERE ${ProfileTableEntry.Column_ProfileUsername} LIKE '$profileUsername'"

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        if (cursor.moveToFirst()) {
            val result = Profile(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) == 1,
            )
            db.close()
            cursor.close()
            return result
        } else {
            db.close()
            cursor.close()
            return Profile(-1, "", "", false)
        }
    }

    fun getSelectedProfile(): Profile {
        val db: SQLiteDatabase = this.readableDatabase
        val criteria = 1
        val sqlStatement =
            "SELECT * FROM ${ProfileTableEntry.ProfileTableName} WHERE ${ProfileTableEntry.Column_ProfileIsSelected} LIKE '$criteria'"

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        if (cursor.moveToFirst()) {
            val result = Profile(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) == 1,
            )
            db.close()
            cursor.close()
            return result
        } else {
            db.close()
            cursor.close()
            return Profile(-1, "", "", false)
        }
    }

    fun getProfiles(): List<Profile> {
        val db: SQLiteDatabase = this.readableDatabase
        val sqlStatement =
            "SELECT * FROM ${ProfileTableEntry.ProfileTableName}"

        val cursor: Cursor = db.rawQuery(sqlStatement, null)
        val profileItems = mutableListOf<Profile>()
        if (cursor.moveToFirst()) {
            do {
                val profileItem = Profile(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3) == 1,
                )
                profileItems += profileItem
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return profileItems
    }

    // functions for TSetting

    fun assignDefaultSettings(db: SQLiteDatabase?) {
        val cv = ContentValues()

        cv.put(SettingTableEntry.Column_SettingDescription, "Pomodoro Timer Duration (Minutes)")
        cv.put(SettingTableEntry.Column_SettingValue, "25")

        db?.insert(
            SettingTableEntry.SettingTableName,
            null,
            cv
        )

        cv.clear()
        cv.put(SettingTableEntry.Column_SettingDescription, "Short Break Timer Duration (Minutes)")
        cv.put(SettingTableEntry.Column_SettingValue, "5")

        db?.insert(
            SettingTableEntry.SettingTableName,
            null,
            cv
        )

        cv.clear()
        cv.put(SettingTableEntry.Column_SettingDescription, "Long Break Timer Duration (Minutes)")
        cv.put(SettingTableEntry.Column_SettingValue, "15")

        db?.insert(
            SettingTableEntry.SettingTableName,
            null,
            cv
        )

        cv.clear()
        cv.put(SettingTableEntry.Column_SettingDescription, "Long Break Interval")
        cv.put(SettingTableEntry.Column_SettingValue, "4")

        db?.insert(
            SettingTableEntry.SettingTableName,
            null,
            cv
        )
    }

    fun resetDefaultSettings() {
        val db: SQLiteDatabase = this.readableDatabase

        db.execSQL("DROP TABLE IF EXISTS ${SettingTableEntry.SettingTableName}")
        db.execSQL(sqlCreateTSettingStatement)

        assignDefaultSettings(db)
        db.close()
    }

    fun updateSetting(settingDescription: String, settingValue: String): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()

        cv.put(SettingTableEntry.Column_SettingValue, settingValue)

        val success = db.update(
            SettingTableEntry.SettingTableName,
            cv,
            "${SettingTableEntry.Column_SettingDescription} = $settingDescription",
            null
        ) == 1
        db.close()
        return success
    }
}