package com.adidas.luissilva.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val GOAL_TABLE = "goal"

@Entity(tableName = GOAL_TABLE)
data class Goal(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id: String,
        @ColumnInfo(name = "description")
        val description: String,
        @ColumnInfo(name = "goal")
        val goal: Int,

        @Embedded
        val reward: Reward,

        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "type")
        val type: String
)