package com.ridwan.management.controllers

import com.ridwan.management.verticles.HttpServerVerticle
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.ext.jdbc.querySingleAwait

class MainController(verticle: HttpServerVerticle) : Controller(verticle) {
    override fun setupRouter() {
        routeGet("/", this::indexAction)
    }

    private suspend fun indexAction(context: RoutingContext) {
        val result = verticle.jdbcClient.querySingleAwait("select count(*) from newtable")
        context.response().end(result?.getLong(0).toString())
    }
}