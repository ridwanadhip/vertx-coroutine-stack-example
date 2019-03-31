package com.ridwan.mvc.extension

import com.ridwan.mvc.constant.ContentType
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

fun RoutingContext.endAsJson(body: Any) {
  if (this.response().ended())
    return
  
  this.response()
    .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
    .end(Json.encode(body))
}

fun RoutingContext.endAsEmptyJson() {
  if (this.response().ended())
    return
  
  this.response()
    .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
    .end(JsonObject().encode())
}

fun RoutingContext.endAsErrorJson(errorCode: Int, message: String) {
  if (this.response().ended())
    return
  
  this.response()
    .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
    .setStatusCode(errorCode)
    .end(json { obj("message" to message) }.encode())
}

fun RoutingContext.endAsHtml(content: String) {
  if (this.response().ended())
    return
  
  this.response()
    .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.HTML)
    .end(content)
}