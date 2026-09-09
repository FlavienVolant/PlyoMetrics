package com.example.plyometrics.repository

import com.example.plyometrics.model.RawJump

interface JumpRepository {

    suspend fun save(jump: RawJump)
    suspend fun getAll(): List<RawJump>
    suspend fun get(id: String): RawJump?
    suspend fun delete(id: String)
}