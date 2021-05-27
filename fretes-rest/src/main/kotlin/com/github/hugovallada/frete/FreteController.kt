package com.github.hugovallada.frete

import com.github.hugovallada.CalculaFreteResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import java.net.http.HttpResponse

@Controller("/api/frete")
class FreteController {


    @Get
    fun calculaFrete(@QueryValue cep: String) : HttpResponse<CalculaFreteResponse> {

    }

}