package com.ridwan.management.controllers

import com.ridwan.management.misc.ContentType
import com.ridwan.management.ext.endAsEmptyJson
import com.ridwan.management.ext.endAsErrorJson
import com.ridwan.management.verticles.HttpServerVerticle
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.ext.sql.updateWithParamsAwait
import java.lang.Exception
import java.util.*

class MainController(verticle: HttpServerVerticle) : Controller(verticle) {
    override fun setupRouter() {
        routeGet("/", this::indexAction)
        routePost("/add-user", this::addUserAction).consumes(ContentType.JSON)
    }

    private suspend fun indexAction(context: RoutingContext) {
        context.response().end("It works!")
    }

    private suspend fun addUserAction(context: RoutingContext) {
        val body = try {
            context.bodyAsJson
        } catch (e: Exception) {
            context.endAsErrorJson(400, "bad input")
            return
        }

        val db = verticle.db
        val auth = verticle.auth
        val salt = auth.generateSalt()
        val param = json { array(
                UUID.randomUUID(),
                body.getString("username"),
                auth.computeHash(body.getString("password"), salt),
                salt,
                body.getString("email"),
                body.getString("role")
        )}

        try {
            db.updateWithParamsAwait("insert into users values(?, ?, ?, ?, ?, ?)", param)
        } catch (e: Exception) {
            context.endAsErrorJson(500, "server error")
            return
        }

        context.endAsEmptyJson()
    }
}