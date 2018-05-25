package tech.cambridgeweblab.service.a

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriBuilderFactory
import org.springframework.web.util.UriComponentsBuilder

/**
 * This class is ...
 * @since 24/05/2018
 */
@RestController
@RequestMapping("/api")
class UberApiController(val serviceB: ServiceBClient, val serviceC: ServiceCClient) {

    @GetMapping(path = ["/chain"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun chain(@RequestParam(name = "s", defaultValue = "Hello, world") str: String): String {
        // Call service B
        val str2 = serviceB.rot13AndUpper(str)
        // Then pass output to service C.
        return "Service chain says: ${str2}"
    }

    @GetMapping(path = ["/parallel"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun parallel(@RequestParam(name = "s", defaultValue = "Hello, world") str: String): String {
        val str2 = async { serviceB.rot13(str) }
        val str3 = async { serviceC.upper(str) }
        return runBlocking {
            "Service B says: ${str2.await()}\nService C says: ${str3.await()}"
        }
    }
}

@Component
class ServiceBClient(val discoveryClient: DiscoveryClient) {
    val restTemplate = RestTemplate()

    fun rot13(str: String): String {
        discoveryClient.getInstances("demo-service-b").first().uri.let {
            return restTemplate.postForObject<String>(it, str, String::class.java)!!
        }
    }

    fun rot13AndUpper(str: String): String {
        discoveryClient.getInstances("demo-service-b").first().uri.let {
            val ub = UriComponentsBuilder.fromUri(it)
            ub.queryParam("chain", true)
            return restTemplate.postForObject<String>(ub.toUriString(), str, String::class.java)!!
        }
    }

}

@Component
class ServiceCClient(val discoveryClient: DiscoveryClient) {
    val restTemplate = RestTemplate()

    fun upper(str: String): String {
        discoveryClient.getInstances("demo-service-c").first().uri.let {
            return restTemplate.postForObject<String>(it, str, String::class.java)!!
        }
    }
}


