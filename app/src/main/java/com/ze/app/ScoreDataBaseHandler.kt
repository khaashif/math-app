package com.ze.app

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast


val DATABASE_NAME = "scoreDB"
val TABLE_NAME = "Scores"
val COL_TOPIC = "Topic"
val COL_LEVEL = "Level"
val COL_XP = "XP"
val COL_XP_NO_HINT = "XP_NO_HINT"
val COL_XP_NO_ANSWER = "XP_NO_ANSWER"
val COL_XP_INCORRECT_ATTEMPTS = "XP_INCORRECT_ATTEMPTS"
val COL_XP_TOTAL = "XP_TOTAL"


class ScoreDataBaseHandler(var context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_TOPIC + " INTEGER," +
                COL_LEVEL + " INTEGER," +
                COL_XP + " INTEGER," +
                COL_XP_NO_HINT + " INTEGER," +
                COL_XP_NO_ANSWER + " INTEGER," +
                COL_XP_INCORRECT_ATTEMPTS + " INTEGER," +
                COL_XP_TOTAL + " INTEGER)"
        db?.execSQL(createTable)
        println("j: table created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        println("j: table upgraded")
    }

    fun insertData(questionScore: QuestionScore) {

        var database = this.readableDatabase
        var existsQuery =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TOPIC + " = " + questionScore.topic + " AND " + COL_LEVEL + " = " + questionScore.level
        var cursor = database.rawQuery(existsQuery, null)

        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_TOPIC, questionScore.topic)
        cv.put(COL_LEVEL, questionScore.level)
        cv.put(COL_XP, questionScore.xp)
        cv.put(COL_XP_NO_HINT, questionScore.xpNoHint)
        cv.put(COL_XP_NO_ANSWER, questionScore.xpNoAnswer)
        cv.put(COL_XP_INCORRECT_ATTEMPTS, questionScore.xpIncorrectAttempts)
        cv.put(COL_XP_TOTAL, questionScore.xpTotal)
        if (cursor.count == 0) {
            var result = db.insert(TABLE_NAME, null, cv)
            println("j: inserted level entry was: " + cv + " " + " result = " + result)
        } else {
            println("j: row already exists, xp already obtained")
        }
        println("j " + DatabaseUtils.queryNumEntries(this.readableDatabase, TABLE_NAME))
        cursor.close()
    }

    fun returnDB() {
        var db = this.readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, TABLE_NAME)
//        db.close()
        println("j: count is " + count)
    }

    fun readTopicScore(topic: Int): Int {
        var topicScoreList: MutableList<QuestionScore> = ArrayList()
        var db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME + " where Topic = " + topic.toString()
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                var questionScore = QuestionScore(
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6),
                    result.getInt(7)
                )
                topicScoreList.add(questionScore)
            } while (result.moveToNext())
        }
        result.close()

        var topicScore: Int
        var sumScore: MutableList<Int> = ArrayList()

        for (i in 0..(topicScoreList.size) - 1) {
            topicScore = (topicScoreList.get(i).xpTotal)
            sumScore.add(topicScore)
        }
        return sumScore.sum()
    }


    fun retrieveXP(topic: Int, level: Int): QuestionScore {
        var topicScoreList: MutableList<QuestionScore> = ArrayList()
        var db = this.readableDatabase
        val query =
            "Select * from " + TABLE_NAME + " where " + COL_TOPIC + " = " + topic.toString() + " and " + COL_LEVEL + " = " + level.toString()
        val result = db.rawQuery(query, null)
        var questionScore: QuestionScore
        if (result.moveToFirst()) {
            questionScore = QuestionScore(
                result.getInt(0),
                result.getInt(1),
                result.getInt(2),
                result.getInt(3),
                result.getInt(4),
                result.getInt(5),
                result.getInt(6)
            )
        } else {
            println("j: retrieve xp failed for topic $topic and level $level")
            questionScore = QuestionScore(0, 0, 0, 0, 0, 0, 0)
        }
        result.close()

        return questionScore
    }


    fun topicScoreHighLevel(n1: Int, n2: Int): Int {
        var topicScoreList: MutableList<QuestionScore> = ArrayList()
        var db = this.readableDatabase
        val query =
            "Select * from " + TABLE_NAME + " where Topic between " + n1.toString() + " and " + n2.toString()
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                var questionScore = QuestionScore(
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6),
                    result.getInt(7)
                )
                topicScoreList.add(questionScore)

            } while (result.moveToNext())
        }
        result.close()

        var topicScore: Int
        var sumScore: MutableList<Int> = ArrayList()

        for (i in 0..(topicScoreList.size) - 1) {
            topicScore = (topicScoreList.get(i).xp)
            sumScore.add(topicScore)
        }

        return sumScore.sum()
    }

    fun totalScoreforLeaderboard(): Int {
        var topicScoreList: MutableList<QuestionScore> = ArrayList()
        var db = this.readableDatabase
        val query = "Select SUM(" + COL_XP + ") from " + TABLE_NAME
        val result = db.rawQuery(query, null)
        var totalScore = 0
        if (result.moveToFirst()) {
            do {
                totalScore = result.getInt(0)
            } while (result.moveToNext())
        }
        result.close()
        return totalScore
    }

    fun retrieveMaxLevel(): List<Int> {
        var db = this.readableDatabase
        val query =
            "Select " + COL_TOPIC + ", " + COL_LEVEL + " from " + TABLE_NAME + " order by " + COL_TOPIC + ", " + COL_LEVEL
        val result = db.rawQuery(query, null)

        var topic: Int
        var level: Int
        if (result.moveToFirst()) {
            result.moveToPosition(result.count - 1)
            if (result.getInt(0) == 1) {
                topic = 2
                level = result.getInt(1)
            } else {
                topic = result.getInt(0)
                level = result.getInt(1)
            }
        } else {
            topic = 2
            level = 1
        }

        result.close()
        println("j: maxlvl is now: " + listOf<Int>(topic, level))
        return listOf<Int>(topic, level)
    }


    fun retrieveMaxLevelSelectedTopic(selectedTopic: Int): Int {
        var db = this.readableDatabase
        val query =
            "Select MAX(" + COL_LEVEL + ")" + " from " + TABLE_NAME + " where " + COL_TOPIC + " = " + selectedTopic.toString()
        val result = db.rawQuery(query, null)
        println("level iszsszzsszs1: " + result)
        var level: Int
        if (result.moveToFirst()) {
            result.moveToPosition(0)
            level = result.getInt(0)
        } else {
            level = 1
        }
        result.close()
        println("level iszsszzsszs: " + level)
        return level
    }
}
