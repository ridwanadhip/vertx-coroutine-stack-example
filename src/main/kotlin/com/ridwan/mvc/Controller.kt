package com.ridwan.mvc

import com.ridwan.mvc.constant.ContentType
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

abstract class Controller(val verticle: ServerVerticle) {
  open val prefix: String = "/"
  private lateinit var router: Router
  
  abstract fun route()
  
  internal fun setup() {
    router = Router.router(verticle.vertx)
    route()
    verticle.router.mountSubRouter(prefix, router)
  }
  
  fun routeGet(path: String, handler: suspend (RoutingContext) -> Unit): Route {
    return router
      .route(HttpMethod.GET, path)
      .produces(ContentType.JSON)
      .coroutineHandler(handler)
  }
  
  fun routePost(path: String, handler: suspend (RoutingContext) -> Unit): Route {
    return router
      .route(HttpMethod.POST, path)
      .produces(ContentType.JSON)
      .consumes(ContentType.JSON)
      .coroutineHandler(handler)
  }
  
  private fun Route.coroutineHandler(handler: suspend (RoutingContext) -> Unit): Route {
    return this.handler { context ->
      verticle.launch(context.vertx().dispatcher()) {
        try {
          handler(context)
        } catch (e: Exception) {
          verticle.logger.error(e.localizedMessage)
          context.fail(e)
        }
      }
    }
  }
}