package com.ridwan.management.ext

import com.google.common.net.HttpHeaders
import com.ridwan.management.misc.ContentType
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

fun RoutingContext.endAsJson(body: Any) {
    this.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .end(Json.encode(body))
}

fun RoutingContext.endAsEmptyJson() {
    this.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .end(JsonObject().encode())
}

fun RoutingContext.endAsErrorJson(errorCode: Int, message: String) {
    this.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
            .setStatusCode(errorCode)
            .end(json { obj("message" to message) }.encode())
}