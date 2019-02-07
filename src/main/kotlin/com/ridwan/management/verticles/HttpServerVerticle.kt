package com.ridwan.management.verticles

import com.ridwan.management.*
import com.ridwan.management.controllers.MainController
import com.ridwan.management.controllers.Controller
import io.netty.util.internal.logging.Log4J2LoggerFactory
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.jdbc.JDBCAuth
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle

class HttpServerVerticle : CoroutineVerticle() {
    private lateinit var httpServer: HttpServer
    private lateinit var router: Router
    lateinit var db: JDBCClient
    lateinit var auth: JDBCAuth
    lateinit var logger: Logger

    override suspend fun start() {
        logger = LoggerFactory.getLogger(Log4J2LoggerFactory::class.java)

        // Setup http server
        val serverOptions = httpServerOptionsOf(host = DOMAIN, port = HTTP_SERVER_PORT)
        httpServer = vertx.createHttpServer(serverOptions)
        router = Router.router(vertx)
        router.route()
                .handler(CookieHandler.create())
                .handler(BodyHandler.create())
                .handler(SessionHandler.create(LocalSessionStore.create(vertx)))
                .handler(LoggerHandler.create(false, LoggerFormat.SHORT))

        // Turn on all controller's router, and attach these as subrouter of main router
        for (controller in getControllers()) {
            controller.setupRouter()
            router.mountSubRouter(controller.prefix, controller.router)
        }

        db = JDBCClient.createShared(vertx, json {
            obj(
                    "provider_class" to "io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider",
                    "driverClassName" to "org.postgresql.Driver",
                    "jdbcUrl" to "jdbc:postgresql://$DBMS_HOST:$DBMS_PORT/$DBMS_DATABASE",
                    "username" to DBMS_USERNAME,
                    "password" to DBMS_PASSWORD,
                    "castUUID" to true
            )
        })

        auth = JDBCAuth.create(vertx, db)
        httpServer.requestHandler(router).listen()
    }

    override suspend fun stop() {
        httpServer.close()
    }

    private fun getControllers(): List<Controller> {
        return listOf(
                MainController(this)
        )
    }
}