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
    val router: Router = Router.router(verticle.vertx)

    abstract fun setupRouter()

    fun routeGet(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return router.route(HttpMethod.GET, path).handler { context ->
            verticle.launch(context.vertx().dispatcher()) {
                try {
                    handler(context)
                } catch (e: Exception) {
                    context.fail(e)
                }
            }
        }
    }

    fun routePost(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return router.route(HttpMethod.POST, path).consumes(ContentType.JSON).handler { context ->
            verticle.launch(context.vertx().dispatcher()) {
                try {
                    handler(context)
                } catch (e: Exception) {
                    context.fail(e)
                }
            }
        }
    }
}