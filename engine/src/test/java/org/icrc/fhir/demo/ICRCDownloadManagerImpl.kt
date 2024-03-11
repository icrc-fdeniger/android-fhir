package org.icrc.fhir.demo

import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.download.DownloadRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import java.util.*
import kotlin.streams.toList


class ICRCDownloadManagerImpl(query: String="Patient?address-city=NAIROBI") : DownloadWorkManager {
    private val queries: List<String> = listOf(query)
    private val urls = LinkedList(queries)
    private val logger = KotlinLogging.logger {}

    override fun toString(): String {
        return "DownloadManager on ${queries[0]}"
    }

    override suspend fun getNextRequest(): DownloadRequest? =
        urls.poll()?.let { DownloadRequest.of(it) }

    override suspend fun getSummaryRequestUrls() =
        queries
            .stream()
            .map { ResourceType.fromCode(it.substringBefore("?")) to it.plus("?_summary=count") }
            .toList()
            .toMap()

    override suspend fun processResponse(response: Resource): Collection<Resource> {
        var bundleCollection: Collection<Resource> = mutableListOf()
        if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
            bundleCollection =
                response.entry
                    .map { it.resource }
        }
        logger.info { "get ${bundleCollection.size} value(s) for ${queries[0]}" }
        return bundleCollection
    }
}