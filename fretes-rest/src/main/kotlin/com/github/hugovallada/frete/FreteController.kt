package com.github.hugovallada.frete

import com.github.hugovallada.CalculaFreteRequest
import com.github.hugovallada.CalculaFreteResponse
import com.github.hugovallada.FretesServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller("/api/frete")
class FreteController(@Inject private val gRpcClient: FretesServiceGrpc.FretesServiceBlockingStub) { // injeta o client , usando a factory


    @Get
    fun calculaFrete(@QueryValue cep: String): HttpResponse<FreteResponse> {
        CalculaFreteRequest.newBuilder() // gera o request do grpc
            .setCep(cep)
            .build().run {
                val response = gRpcClient.calculaFrete(this) // executa o método do gRpc
                return HttpResponse.ok(FreteResponse(response))
            }
    }

}