package tech.cambridgeweblab.service.c

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


/**
 * This class is ...
 * @since 24/05/2018
 */
@RestController
@RequestMapping("/")
class UpperController {

    @PostMapping(consumes = [MediaType.TEXT_PLAIN_VALUE], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun rot13(@RequestBody str: String): String {
        return str.toUpperCase()
    }
}
