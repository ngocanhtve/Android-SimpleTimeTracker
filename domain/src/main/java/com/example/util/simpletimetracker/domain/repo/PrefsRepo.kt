package com.example.util.simpletimetracker.domain.repo

interface PrefsRepo {

    var recordTypesFilteredOnChart: Set<String>

    var sortRecordTypesByColor: Boolean

    fun setWidget(widgetId: Int, recordType: Long)

    fun getWidget(widgetId: Int): Long

    fun removeWidget(widgetId: Int)
}