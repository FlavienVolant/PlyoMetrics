package com.example.plyometrics.serializer

import com.example.plyometrics.model.RawJump

interface JumpSerializer {
    fun serialize(jump: RawJump): String
    fun deserialize(data: String): RawJump
}