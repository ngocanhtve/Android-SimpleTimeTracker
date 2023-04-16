package com.example.util.simpletimetracker.domain.interactor

import com.example.util.simpletimetracker.domain.mapper.StatisticsMapper
import com.example.util.simpletimetracker.domain.model.Record
import com.example.util.simpletimetracker.domain.model.RecordTag
import com.example.util.simpletimetracker.domain.model.Statistics
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatisticsTagInteractor @Inject constructor(
    private val recordInteractor: RecordInteractor,
    private val recordTagInteractor: RecordTagInteractor,
    private val statisticsMapper: StatisticsMapper
) {

    suspend fun getAll(): List<Statistics> = withContext(Dispatchers.IO) {
        val allRecords = recordInteractor.getAll() // TODO expensive, get by filter

        getTagRecords(allRecords)
            .map { (tagId, records) ->
                Statistics(
                    id = tagId,
                    duration = records.let(statisticsMapper::mapToDuration)
                )
            }
    }

    suspend fun getFromRange(
        start: Long,
        end: Long
    ): List<Statistics> = withContext(Dispatchers.IO) {
        val allRecords = recordInteractor.getFromRange(start, end)

        getTagRecords(allRecords)
            .map { (tagId, records) ->
                Statistics(
                    id = tagId,
                    duration = statisticsMapper.mapToDurationFromRange(records, start, end)
                )
            }
    }

    private suspend fun getTagRecords(allRecords: List<Record>): Map<Long, List<Record>> {
        val recordTags = recordTagInteractor.getAll().map(RecordTag::id)

        return recordTags
            .associateWith { tagId -> allRecords.filter { tagId in it.tagIds } }
            .filterValues(List<Record>::isNotEmpty)
    }
}