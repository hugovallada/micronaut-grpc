package com.github.hugovallada.frete

import com.github.hugovallada.CalculaFreteRequest
import com.github.hugovallada.CalculaFreteResponse
import com.github.hugovallada.ErrorDetails
import com.github.hugovallada.FretesServiceGrpc
import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller("/api/frete")
class FreteController(@Inject private val gRpcClient: FretesServiceGrpc.FretesServiceBlockingStub) { // injeta o client , usando a factory


    @Get
    fun calculaFrete(@QueryValue cep: String): HttpResponse<FreteResponse> {

        try{
            CalculaFreteRequest.newBuilder() // gera o request do grpc
                .setCep(cep)
                .build().run {
                    val response = gRpcClient.calculaFrete(this) // executa o método do gRpc
                    return HttpResponse.ok(FreteResponse(response))
                }
        } catch (e: StatusRuntimeException){ // captura o erro de status run time
            val description = e.status.description // pega a descrição dentro do Status
            val statusCode = e.status.code // pega o code dentro do Status

            // Você deve sempre mapear o erro grpc para um erro REST
            when(statusCode) {
                Status.Code.INVALID_ARGUMENT -> throw HttpStatusException(HttpStatus.BAD_REQUEST,description) // retorna o erro para um erro similar no Http
                Status.Code.PERMISSION_DENIED -> {
                    val statusProto = StatusProto.fromThrowable(e) // pega o Status Proto que foi usado para ter o ErroDetails
                    if(statusProto == null) {
                        throw HttpStatusException(HttpStatus.FORBIDDEN, description) // se não existir, trata como um erro normal
                    }

                    val anyDetails: Any = statusProto.detailsList.get(0) // pega o primeiro valor extra adicionado na lista
                    val errorDetails = anyDetails.unpack(ErrorDetails::class.java) // desempacota para a classe customizada

                    throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code} : ${errorDetails.message}")
                }
                else ->  throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)  // Uma opção é usar o controller advice para ter um retorno melhor
            }
        }
    }

}