package com.ankara.bote.randevu.data.dao

import androidx.room.*
import com.ankara.bote.randevu.data.model.Group
import com.ankara.bote.randevu.data.model.GroupMember
import com.ankara.bote.randevu.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: GroupMember)

    @Query("""
        SELECT g.id, g.name, g.ownerId FROM `groups` g
        INNER JOIN group_members gm ON g.id = gm.groupId
        WHERE gm.studentId = :studentId
    """)
    fun getGroupsForStudent(studentId: Int): Flow<List<Group>>

    @Query("""
        SELECT s.* FROM students s
        INNER JOIN group_members gm ON s.id = gm.studentId
        WHERE gm.groupId = :groupId
    """)
    suspend fun getMembersOfGroup(groupId: Int): List<Student>

    @Query("DELETE FROM `groups` WHERE id = :groupId")
    suspend fun deleteGroup(groupId: Int)
}