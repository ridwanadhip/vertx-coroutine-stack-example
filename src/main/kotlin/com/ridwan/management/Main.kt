@file:JvmName("Main")

package com.ridwan.management

import com.ridwan.management.verticle.MainVerticle
import io.vertx.core.Vertx

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}