package com.github.hugovallada.frete

import com.github.hugovallada.FretesServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory // indica para o micronaut que essa classe é uma Factory
class GrpcClientFactory {

    /**
     * Esse método cria o blocking stub que será o client
     */
    @Singleton // indica para o micronaut que o método será gerenciado por ele
    fun fretesClientStub(@GrpcChannel("fretes") channel: ManagedChannel): FretesServiceGrpc.FretesServiceBlockingStub? {
        return FretesServiceGrpc.newBlockingStub(channel) // retorna um client
    }
}