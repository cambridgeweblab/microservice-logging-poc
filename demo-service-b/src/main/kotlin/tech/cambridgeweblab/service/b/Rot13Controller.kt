package tech.cambridgeweblab.service.b

import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import jdk.nashorn.tools.ShellFunctions.input
import org.bouncycastle.asn1.x500.style.RFC4519Style.c


/**
 * This class is ...
 * @since 24/05/2018
 */
@RestController
@RequestMapping("/")
class Rot13Controller(val serviceC: ServiceCClient) {

    @PostMapping(consumes = [MediaType.TEXT_PLAIN_VALUE], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun rot13(@RequestBody str: String, @RequestParam(value = "chain", defaultValue = "false") chain: Boolean): String {
        val rot13 = String(str.map { c ->
            when {
                c in 'a'..'m' -> c + 13
                c in 'A'..'M' -> c + 13
                c in 'n'..'z' -> c - 13
                c in 'N'..'Z' -> c - 13
                else -> c
            }
        }.toCharArray())
        if (chain) {
            return serviceC.upper(rot13)
        }
        return rot13
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
