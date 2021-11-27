package com.github.jazzschmidt.spring.jsonvalidation

import com.github.jazzschmidt.spring.jsonvalidation.web.ValidateJsonContent
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class ValidatorTest extends ApplicationTest {

    @RestController
    @RequestMapping("/test")
    protected static class TestController {
        @PostMapping
        @ValidateJsonContent
        HashMap<String, Object> post(@RequestBody HashMap<String, Object> json) {
            return json
        }
    }

}
