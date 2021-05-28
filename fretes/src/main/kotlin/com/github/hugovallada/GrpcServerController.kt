package com.github.hugovallada

import io.micronaut.grpc.server.GrpcEmbeddedServer
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import javax.inject.Inject

@Controller
class GrpcServerController(@Inject val grpcServer: GrpcEmbeddedServer) { // server do grpc do micronaut

    @Get("/grpc-server")
    fun stop(): HttpResponse<String>{

        grpcServer.stop() // para o grpc server

        return HttpResponse.ok("is running? ${grpcServer.isRunning}")
    }
}