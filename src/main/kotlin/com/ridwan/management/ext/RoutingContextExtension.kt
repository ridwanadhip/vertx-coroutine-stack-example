package com.ridwan.management.ext

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

fun RoutingContext.endAsJson(body: Any) {
    this.response().putHeader("Content-Type", "application/json; charset=utf-8")
            .end(Json.encode(body))
}

fun RoutingContext.endAsEmptyJson() {
    this.response().putHeader("Content-Type", "application/json; charset=utf-8")
            .end(JsonObject().encode())
}

fun RoutingContext.endAsErrorJson(errorCode: Int, message: String) {
    this.response().putHeader("Content-Type", "application/json; charset=utf-8")
            .setStatusCode(errorCode)
            .end(json { obj("message" to message) }.encode())
}