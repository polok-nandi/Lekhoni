package com.example.data

import com.example.model.LekhoniDocument
import com.example.model.StarterTemplates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DocumentRepository(private val documentDao: DocumentDao) {

    val allDocuments: Flow<List<LekhoniDocument>> = documentDao.getAllDocuments()
        .map { entities -> entities.map { it.toLekhoniDocument() } }

    val starredDocuments: Flow<List<LekhoniDocument>> = documentDao.getStarredDocuments()
        .map { entities -> entities.map { it.toLekhoniDocument() } }

    fun searchDocuments(query: String): Flow<List<LekhoniDocument>> {
        return documentDao.searchDocuments(query)
            .map { entities -> entities.map { it.toLekhoniDocument() } }
    }

    suspend fun getDocumentById(id: String): LekhoniDocument? {
        return documentDao.getDocumentById(id)?.toLekhoniDocument()
    }

    suspend fun saveDocument(document: LekhoniDocument) {
        val updated = document.copy(updatedAt = System.currentTimeMillis())
        val entity = DocumentEntity.fromLekhoniDocument(updated)
        documentDao.insert(entity)
    }

    suspend fun deleteDocument(id: String) {
        documentDao.deleteById(id)
    }

    suspend fun toggleStarred(id: String, isStarred: Boolean) {
        documentDao.updateStarred(id, isStarred)
    }

    suspend fun renameDocument(id: String, newTitle: String) {
        documentDao.renameDocument(id, newTitle, System.currentTimeMillis())
    }

    suspend fun duplicateDocument(document: LekhoniDocument): LekhoniDocument {
        val duplicated = document.copy(
            id = UUID.randomUUID().toString(),
            title = "${document.title} (কপি)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isStarred = false
        )
        saveDocument(duplicated)
        return duplicated
    }

    suspend fun seedInitialDataIfNeeded() {
        if (documentDao.getDocumentCount() == 0) {
            // Seed welcome letter & sample official letter
            val welcomeDoc = StarterTemplates.templates.find { it.id == "official_letter" }?.documentFactory?.invoke()
            if (welcomeDoc != null) {
                saveDocument(welcomeDoc)
            }
            val businessDoc = StarterTemplates.templates.find { it.id == "business_report" }?.documentFactory?.invoke()
            if (businessDoc != null) {
                saveDocument(businessDoc)
            }
        }
    }

    suspend fun getTotalStorageBytes(): Long {
        return documentDao.getTotalStorageBytes() ?: 0L
    }

    suspend fun getDocumentCount(): Int {
        return documentDao.getDocumentCount()
    }
}
