package com.github.hugovallada

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import javax.inject.Singleton
import kotlin.random.Random

@Singleton // Anotação para que o micronaut controle a classe
class FreteGrpcServer : FretesServiceGrpc.FretesServiceImplBase() { // herda da ImplBase do ServiceGrpc gerado


    private val LOG = LoggerFactory.getLogger(FreteGrpcServer::class.java)

    /**
     * Sobrescreve o método definido no proto, recebendo um request, e retornando um stream observer que contém o seu response
     */
    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        LOG.info("Requisição recebida")

        val cep = request?.cep

        if (cep == null || cep.isBlank()) {
            val e = Status.INVALID_ARGUMENT.withDescription("O cep deve ser informado")
                .asRuntimeException() // gera o status e retorna como uma exceção de runtime

            responseObserver?.onError(e) // Por segurança é necessário ser explicito com o erro
        }


        if(!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())){
            Status.INVALID_ARGUMENT // adiciona o status do erro
                .withDescription("O cep é inválido") // adiciona a descrição do erro
                .augmentDescription("O formato esperado é XXXXX-XXX") // adiciona uma descrição adicional
                .asRuntimeException() // define que é um tipo de exceção runtime
                .run {
                    responseObserver?.onError(this) // chama o onError com prog funcional
                }
        }


        val response = CalculaFreteResponse.newBuilder() // grpc usa muito o builder
            .setCep(request?.cep)
            .setValor(Random.nextDouble(from = 0.00, until = 150.00))
            .build()


        LOG.info("Frete calculado e retornado")
        responseObserver!!.onNext(response) // retorna o response
        responseObserver.onCompleted() // fecha o observer
    }

}