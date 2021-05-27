package com.github.hugovallada

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
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
        val response = CalculaFreteResponse.newBuilder() // grpc usa muito o builder
            .setCep(request?.cep)
            .setValor(Random.nextDouble(from = 0.00, until = 150.00))
            .build()


        LOG.info("Frete calculado e retornado")
        responseObserver!!.onNext(response) // retorna o response
        responseObserver.onCompleted() // fecha o observer
    }

}