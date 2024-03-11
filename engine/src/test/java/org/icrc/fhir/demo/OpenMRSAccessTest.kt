package org.icrc.fhir.demo

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.ErrorHandlerAdapter
import ca.uhn.fhir.parser.JsonParser
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.HttpAuthenticationMethod
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.android.fhir.sync.download.DownloadState
import com.google.android.fhir.sync.download.Downloader
import com.google.android.fhir.sync.download.DownloaderImpl
import com.google.android.fhir.sync.remote.FhirHttpDataSource
import com.google.android.fhir.sync.remote.FhirHttpService
import com.google.android.fhir.sync.remote.HttpLogger
import com.google.android.fhir.sync.remote.RetrofitHttpService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Resource
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.FileInputStream
import java.util.Properties


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class OpenMRSAccessTest {

    private val logger = KotlinLogging.logger {}
    private lateinit var retrofitHttpService : FhirHttpService


    @Before
    fun load_variables() {
        val openmrsFileProperties = File("./openmrs.properties").absoluteFile;
        if (!openmrsFileProperties.exists()) {
            Assert.fail("file ${openmrsFileProperties.path} can't be found");
        }
        val properties = Properties()
        FileInputStream(openmrsFileProperties).use {
            properties.load(it);
        }
        val openmrsUser: String = properties.getProperty("openmrsUser", "")
        val openmrsPwd: String = properties.getProperty("openmrsPwd", "")
        val openmrsUrl: String = properties.getProperty("openmrsUrl", "")
        var valid: Boolean = true;
        if (openmrsUser.isBlank()) {
            logger.error { "the property openmrsUser is not set in ${openmrsFileProperties.path}" }
            valid = false;
        }
        if (openmrsPwd.isBlank()) {
            logger.error { "the property openmrsPwd is not set in ${openmrsFileProperties.path}" }
            valid = false;
        }
        if (openmrsUrl.isBlank()) {
            logger.error { "the property openmrsUrl is not set in ${openmrsFileProperties.path}" }
            valid = false;
        }
        if (!valid) {
            Assert.fail("Some required properties are not set");
        } else {
            logger.info { "Can configure HttpService" }
        }
        retrofitHttpService =
            RetrofitHttpService.builder(
                openmrsUrl,
                NetworkConfiguration(uploadWithGzip = false)
            )
                .setAuthenticator {
                    HttpAuthenticationMethod.Basic(
                        username = openmrsUser,
                        password = openmrsPwd
                    )
                }
                .setHttpLogger(HttpLogger.NONE)
                .build()

    }


    @Test
    fun test_connection_to_openmrs() {
        val fhirHttpDataSource = FhirHttpDataSource(retrofitHttpService);
        val downloadWorkManager = ICRCDownloadManagerImpl()
        val downloader = DownloaderImpl(fhirHttpDataSource, downloadWorkManager)
        val fhirContext = FhirContext(FhirVersionEnum.R4)
        val jsonParser = JsonParser(fhirContext, ErrorHandlerAdapter())
        val extractResources = extractResources(downloader,downloadWorkManager.toString())
        extractResources.forEach { r -> System.err.println(jsonParser.encodeToString(r)) }

    }

    private fun extractResources(
        downloader: Downloader, url: String
    ): List<Resource> {
        val resources = mutableListOf<Resource>()
        runBlocking {
            downloader.download().collect {
                when (it) {
                    is DownloadState.Started -> {
                        logger.debug { "Download started" }
                    }

                    is DownloadState.Success -> {
                        resources.addAll(it.resources)
                    }

                    is DownloadState.Failure -> {
                        logger.error { "can't download resource $url" }
                        throw it.syncError.exception
                    }
                }
            };
        }
        return resources
    }
}