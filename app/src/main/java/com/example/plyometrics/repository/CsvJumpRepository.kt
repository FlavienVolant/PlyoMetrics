package com.example.plyometrics.repository

import android.content.Context
import com.example.plyometrics.serializer.CsvJumpSerializer
import com.example.plyometrics.model.RawJump
import java.io.File

class CsvJumpRepository(context: Context): JumpRepository {

    private val serializer = CsvJumpSerializer()

    private val jumpsDirectory = File(context.filesDir, "jumps").apply {
        mkdirs()
    }

    override suspend fun save(jump: RawJump) {
        val file = File(jumpsDirectory, "${jump.id}.csv")

        file.writeText(serializer.serialize(jump))
    }

    override suspend fun getAll(): List<RawJump> {
        return jumpsDirectory
            .listFiles { file ->
                file.extension == "csv"
            }
            ?.mapNotNull { file ->
                runCatching {
                    serializer.deserialize(file.readText())
                }.getOrNull()
            }
            ?: emptyList()
    }

    override suspend fun get(id: String): RawJump? {
        val file = File(jumpsDirectory, "$id.csv")

        if(!file.exists()) {
            return null
        }

        return serializer.deserialize(file.readText())
    }

    override suspend fun delete(id: String) {
        val file = File(jumpsDirectory, "$id.csv")

        file.delete()
    }
}