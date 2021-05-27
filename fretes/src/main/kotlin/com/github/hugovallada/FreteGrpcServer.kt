package com.github.hugovallada

import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
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

        val cep = request?.cep

        if (cep == null || cep.isBlank()) {
            val e = Status.INVALID_ARGUMENT.withDescription("O cep deve ser informado")
                .asRuntimeException() // gera o status e retorna como uma exceção de runtime

            responseObserver?.onError(e) // Por segurança é necessário ser explicito com o erro
        }


        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())) {
            Status.INVALID_ARGUMENT // adiciona o status do erro
                .withDescription("O cep é inválido") // adiciona a descrição do erro
                .augmentDescription("O formato esperado é XXXXX-XXX") // adiciona uma descrição adicional
                .asRuntimeException() // define que é um tipo de exceção runtime
                .run {
                    responseObserver?.onError(this) // chama o onError com prog funcional
                }
        }

        // SIMULAR uma verificação de segurança
        if (cep.endsWith("333")) {
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuário não pode acessar esse recurso")
                .addDetails( // Adiciona erros customizados
                    Any.pack( // O Any é uma classe do protobuf que pode empacotar qualquer tipo
                        ErrorDetails.newBuilder() // O Google ja tem alguns detalhes de erros a serem usados : https://cloud.google.com/apis/design/errors
                            .setCode(401)
                            .setMessage("token expirado")
                            .build()
                    )
                )
                .build()

            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        var valor = 0.0
        try {
            valor = Random.nextDouble(from = 0.0, until = 150.00)
            if (valor > 100) throw IllegalStateException("Erro inesperado")
        } catch (e: Exception) {
            Status.INTERNAL
                .withDescription(e.message)
                .withCause(e.cause) // manda a causa, mas apenas para  server, não retorna para o client
                .asRuntimeException().run {
                    responseObserver?.onError(this)
                }
        }


        val response = CalculaFreteResponse.newBuilder() // grpc usa muito o builder
            .setCep(request.cep)
            .setValor(valor)
            .build()


        LOG.info("Frete calculado e retornado")
        responseObserver!!.onNext(response) // retorna o response
        responseObserver.onCompleted() // fecha o observer
    }

}