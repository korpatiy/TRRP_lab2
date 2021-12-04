package com.trrp.server.module.service.listeners

import com.trrp.server.grpc.MigrationGRPCServiceGrpc
import com.trrp.server.grpc.Reply
import com.trrp.server.grpc.Request
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class GRPCListener : MigrationGRPCServiceGrpc.MigrationGRPCServiceImplBase() {

    override fun migrateData(request: Request?, responseObserver: StreamObserver<Reply>?) {
        val toString = request.toString()
    }
}