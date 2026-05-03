package com.example.simplegymtracker.data.repository

import com.example.simplegymtracker.data.dao.UserDao
import com.example.simplegymtracker.data.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAll()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getById(id)
    }
}
