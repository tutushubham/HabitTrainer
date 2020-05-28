package com.tutushubham.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.database.sqlite.transaction
import com.tutushubham.habittrainer.Habit
import com.tutushubham.habittrainer.db.HabitEntry.DESCR_COL
import com.tutushubham.habittrainer.db.HabitEntry.IMAGE_COL
import com.tutushubham.habittrainer.db.HabitEntry.TABLE_NAME
import com.tutushubham.habittrainer.db.HabitEntry.TITLE_COL
import com.tutushubham.habittrainer.db.HabitEntry._ID
import java.io.ByteArrayOutputStream


class HabitDbTable(context: Context) {

    private val dbHelper = HabitTrainerDb(context)

    fun store(habit: Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()

        with(values) {
            put(TITLE_COL, habit.title)
            put(DESCR_COL, habit.description)
            put(IMAGE_COL, toByteArray(habit.image))
        }

        val id = db.transact { insert(TABLE_NAME, null, values) }

        return id
    }

    fun readAllHabits(): List<Habit>{
        val columns = arrayOf(_ID, TITLE_COL, DESCR_COL, IMAGE_COL)
        val db = dbHelper.readableDatabase

        val order = "$_ID ASC"

        val cursor = db.query(TABLE_NAME, columns, null, null, null,null,order)
        val habits = mutableListOf<Habit>()
        while(cursor.moveToNext()){
            val title = cursor.getString(TITLE_COL)
            val descr = cursor.getString(DESCR_COL)
            val byteArray = cursor.getBlob(cursor.getColumnIndex(IMAGE_COL))
            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            habits.add(Habit(title, descr, bitmap))
        }
        cursor.close()

        return habits

    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

}

private fun Cursor.getString(columnName: String)  = getString(getColumnIndex(columnName))

private inline fun <T> SQLiteDatabase.transact(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    } finally {
        endTransaction()
    }
    close()
    return result
}