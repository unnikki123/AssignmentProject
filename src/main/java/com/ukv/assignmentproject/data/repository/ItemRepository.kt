package com.ukv.assignmentproject.data.repository

import com.google.gson.Gson
import com.ukv.assignmentproject.data.db.ItemDao
import com.ukv.assignmentproject.data.db.DeletedItemDao
import com.ukv.assignmentproject.data.model.ItemEntity
import com.ukv.assignmentproject.data.model.DeletedItemEntity
import com.ukv.assignmentproject.data.network.ApiService

class ItemRepository(
    private val api: ApiService,
    private val itemDao: ItemDao,
    private val deletedItemDao: DeletedItemDao
) {
    private val gson = Gson()

    suspend fun fetchAndSaveItems() {
        val deletedIds = deletedItemDao.getAllIds().toSet()
        val modifiedIds = itemDao.getAll().filter { it.isModifiedLocally }.map { it.id }.toSet()

        val items = api.getObjects()
            .filterNot { deletedIds.contains(it.id) || modifiedIds.contains(it.id) }
            .map {
                val dataJson = it.data?.let { dataMap -> gson.toJson(dataMap) }
                ItemEntity(
                    id = it.id,
                    name = it.name,
                    dataJson = dataJson
                )
            }

        itemDao.insertAll(items)
    }

    suspend fun getItems(): List<ItemEntity> = itemDao.getAll()

    suspend fun updateItem(item: ItemEntity) {
        val updated = item.copy(isModifiedLocally = true)
        itemDao.updateItem(updated)
    }

    suspend fun deleteItem(item: ItemEntity) {
        itemDao.deleteItem(item)
        deletedItemDao.insert(
            DeletedItemEntity(
                id = item.id,
                name = item.name,
                dataJson = item.dataJson
            )
        )

        try {
            api.sendDeleteNotification(
                id = item.id,
                name = item.name,
                dataJson = item.dataJson ?: "{}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
