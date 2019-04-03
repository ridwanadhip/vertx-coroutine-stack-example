package com.ridwan.mvc

import io.vertx.core.http.HttpServer
import io.vertx.core.logging.Logger
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.http.closeAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.sql.closeAwait

abstract class ServerVerticle : CoroutineVerticle() {
  abstract val httpServer: HttpServer
  abstract val router: Router
  abstract val db: SQLClient
  abstract val auth: AuthProvider
  abstract val logger: Logger
  abstract val controllers: Array<Controller>
  
  abstract suspend fun prepare()
  
  override suspend fun start() {
    prepare()
    
    for (controller in controllers) {
      controller.setup()
    }
    
    httpServer.requestHandler(router).listenAwait()
  }
  
  override suspend fun stop() {
    httpServer.closeAwait()
    db.closeAwait()
  }
}