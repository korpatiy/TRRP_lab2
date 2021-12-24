package com.trrp.server.module.service.listeners

import com.trrp.server.grpc.MigrationGRPCServiceGrpc
import com.trrp.server.grpc.Reply
import com.trrp.server.grpc.Request
import com.trrp.server.module.service.MigrationService
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

/*
@Configuration
class GRPCcfg(){
    @Bean
    fun startServer(migrationService: MigrationService): Server? {
        val server: Server = ServerBuilder.forPort(8443)
            .useTransportSecurity(File("C:\\Users\\Slava\\Desktop\\TRRP_lab2\\server\\src\\main\\resources\\certs\\server.crt"),
                File("C:\\Users\\Slava\\Desktop\\TRRP_lab2\\server\\src\\main\\resources\\certs\\server.key"))
            .addService(GRPCListener(migrationService))
            .build()
        return server.start()
    }
}
*/

@GrpcService
class GRPCListener(
    private val migrationService: MigrationService
) : MigrationGRPCServiceGrpc.MigrationGRPCServiceImplBase() {

    override fun migrateData(responseObserver: StreamObserver<Reply>?): StreamObserver<Request> =
        object : StreamObserver<Request> {
            override fun onNext(value: Request?) {
                value?.let { migrationService.migrate(it) }
            }

            override fun onError(t: Throwable?) {
                t?.message
            }

            override fun onCompleted() {
                responseObserver?.onNext(
                    Reply.newBuilder()
                        .setMessage("Данные успешно перенесены")
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }
}