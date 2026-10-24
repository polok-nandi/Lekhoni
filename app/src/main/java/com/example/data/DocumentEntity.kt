package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.LekhoniDocument

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val contentJson: String,
    val plainText: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isStarred: Boolean = false,
    val fileSizeBytes: Long = 0L,
    val format: String = "LEKHONI"
) {
    fun toLekhoniDocument(): LekhoniDocument {
        return LekhoniDocument.fromJson(contentJson).copy(
            id = id,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isStarred = isStarred
        )
    }

    companion object {
        fun fromLekhoniDocument(doc: LekhoniDocument): DocumentEntity {
            val json = doc.toJson()
            return DocumentEntity(
                id = doc.id,
                title = doc.title,
                contentJson = json,
                plainText = doc.toPlainText(),
                createdAt = doc.createdAt,
                updatedAt = doc.updatedAt,
                isStarred = doc.isStarred,
                fileSizeBytes = json.toByteArray(Charsets.UTF_8).size.toLong(),
                format = "LEKHONI"
            )
        }
    }
}
