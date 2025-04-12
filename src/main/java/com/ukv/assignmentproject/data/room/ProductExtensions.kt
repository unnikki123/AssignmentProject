package com.ukv.assignmentproject.data.room

import com.google.gson.Gson
import com.ukv.assignmentproject.data.model.ItemEntity


import com.ukv.assignmentproject.data.model.ApiObject

fun ApiObject.toEntity(): ItemEntity {
    val dataJson = Gson().toJson(this.data)
    return ItemEntity(
        id = this.id,
        name = this.name,
        dataJson = dataJson
    )
}

