package com.example.plyometrics.serializer

class CsvJumpSerializerTest: JumpSerializerTest() {
    override val serializer: JumpSerializer = CsvJumpSerializer()
}