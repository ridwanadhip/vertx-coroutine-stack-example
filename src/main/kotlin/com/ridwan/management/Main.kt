@file:JvmName("Main")

package com.ridwan.management

import com.ridwan.management.verticles.HttpServerVerticle
import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(HttpServerVerticle())
}