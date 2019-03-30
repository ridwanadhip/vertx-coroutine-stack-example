package com.ridwan.management.verticle

import com.ridwan.management.*
import com.ridwan.management.controller.*
import com.ridwan.mvc.Controller
import com.ridwan.mvc.ServerVerticle
import io.netty.util.internal.logging.Log4J2LoggerFactory
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.jdbc.JDBCAuth
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class MainVerticle : ServerVerticle() {
    override lateinit var httpServer: HttpServer
    override lateinit var router: Router
    override lateinit var db: SQLClient
    override lateinit var auth: AuthProvider
    override lateinit var logger: Logger

    override fun prepare() {
        logger = LoggerFactory.getLogger(Log4J2LoggerFactory::class.java)

        val serverOptions = httpServerOptionsOf(host = DOMAIN, port = HTTP_SERVER_PORT)
        httpServer = vertx.createHttpServer(serverOptions)

        router = Router.router(vertx)
        router.route()
                .handler(CookieHandler.create())
                .handler(BodyHandler.create())
                .handler(SessionHandler.create(LocalSessionStore.create(vertx)))
                .handler(LoggerHandler.create(false, LoggerFormat.SHORT))

        val jdbc = JDBCClient.createShared(vertx, json {
            obj(
                    "provider_class" to "io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider",
                    "driverClassName" to "org.postgresql.Driver",
                    "jdbcUrl" to "jdbc:postgresql://$DBMS_HOST:$DBMS_PORT/$DBMS_DATABASE",
                    "username" to DBMS_USERNAME,
                    "password" to DBMS_PASSWORD,
                    "castUUID" to true
            )
        })

        auth = JDBCAuth.create(vertx, jdbc)
        db = jdbc
    }

    override fun getControllers(): List<Controller> {
        return listOf(
                MainController(this)
        )
    }
}