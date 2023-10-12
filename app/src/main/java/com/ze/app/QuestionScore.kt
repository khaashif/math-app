package com.ze.app

class QuestionScore {

    var topic: Int = 1
    var level: Int = 1
    var xp = 0
    var xpNoHint = 0
    var xpNoAnswer = 0
    var xpIncorrectAttempts = 0
    var xpTotal = 0

    constructor(
        topic: Int,
        level: Int,
        xp: Int,
        xpNoHint: Int,
        xpNoAnswer: Int,
        xpIncorrectAnswer: Int,
        xpTotal: Int
    ) {
        this.topic = topic
        this.level = level
        this.xp = xp
        this.xpNoHint = xpNoHint
        this.xpNoAnswer = xpNoAnswer
        this.xpIncorrectAttempts = xpIncorrectAnswer
        this.xpTotal = xpTotal
    }
}