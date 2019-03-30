package com.ridwan.mvc

import io.vertx.core.http.HttpServer
import io.vertx.core.logging.Logger
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle

abstract class ServerVerticle : CoroutineVerticle() {
    abstract val httpServer: HttpServer
    abstract val router: Router
    abstract val db: SQLClient
    abstract val auth: AuthProvider
    abstract val logger: Logger

    override suspend fun start() {
        prepare()

        for (controller in getControllers()) {
            controller.setupRouter()
            router.mountSubRouter(controller.prefix, controller.router)
        }

        httpServer.requestHandler(router).listenAwait()
    }

    override suspend fun stop() {
        httpServer.close()
        db.close()
    }

    abstract fun prepare()

    abstract fun getControllers(): List<Controller>
}