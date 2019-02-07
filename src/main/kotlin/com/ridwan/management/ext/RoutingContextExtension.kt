package com.ridwan.management.ext

import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

fun RoutingContext.endAsJson(body: Any) {
    this.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encode(body))
}