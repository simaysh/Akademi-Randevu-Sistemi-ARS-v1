package com.ankara.bote.randevu.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "group_members",
    primaryKeys = ["groupId", "studentId"],
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GroupMember(
    val groupId: Int,
    val studentId: Int
)