package com.ridwan.management.controller

import com.ridwan.management.extension.limit
import com.ridwan.management.utility.generateRandomString
import com.ridwan.management.utility.hashPassword
import com.ridwan.management.verticle.MainVerticle
import com.ridwan.mvc.Controller
import com.ridwan.mvc.extension.endAsEmptyJson
import com.ridwan.mvc.extension.endAsErrorJson
import com.ridwan.mvc.extension.endAsJson
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.ext.sql.queryWithParamsAwait
import io.vertx.kotlin.ext.sql.updateWithParamsAwait
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import java.util.*

class MainController(verticle: MainVerticle) : Controller(verticle) {
  override fun route() {
    routeGet("/", this::indexAction)
    routeGet("/user", this::getUsersAction)
    routeGet("/user/:id", this::getUserByIdAction)
    routePost("/user", this::addUserAction)
  }
  
  private suspend fun indexAction(context: RoutingContext) {
    context.response().end("It works!")
  }
  
  private suspend fun getUsersAction(context: RoutingContext) {
    val query = DSL.using(SQLDialect.POSTGRES)
      .select(field("id"), field("username"), field("email"), field("role"))
      .from(table("users"))
      .where(field("is_deleted").eq("?"))
      .sql
    
    val parameters = json { array(false) }
    val result = try {
      verticle.db.queryWithParamsAwait(query, parameters)
    } catch (e: Exception) {
      context.endAsErrorJson(500)
      return
    }
    
    context.endAsJson(result.rows)
  }
  
  private suspend fun getUserByIdAction(context: RoutingContext) {
    val userId = context.pathParam("id")
    val query = DSL.using(SQLDialect.POSTGRES)
      .select(field("id"), field("username"), field("email"), field("role"))
      .from(table("users"))
      .where(field("id").eq("?")
        .and(field("is_deleted").eq("?")))
      .limit("?")
      .sql
    
    val parameters = json { array(userId, false, 1) }
    val result = try {
      verticle.db.queryWithParamsAwait(query, parameters)
    } catch (e: Exception) {
      context.endAsErrorJson(500)
      return
    }
    
    if (result.numRows == 0) {
      context.endAsErrorJson(204)
    } else {
      context.endAsJson(result.rows.first())
    }
  }
  
  private suspend fun addUserAction(context: RoutingContext) {
    val body = try {
      context.bodyAsJson
    } catch (e: Exception) {
      context.endAsErrorJson(400)
      return
    }
    
    val password = body.getString("password")
    val salt = generateRandomString(128)
    val hashedPassword = hashPassword(password, salt)
    
    val parameters = json {
      array(
        UUID.randomUUID().toString(),
        body.getString("username"),
        hashedPassword,
        salt,
        body.getString("email"),
        body.getString("role")
      )
    }
    
    val query = DSL.using(SQLDialect.POSTGRES)
      .insertInto(table("users"))
      .set(field("id"), "?")
      .set(field("username"), "?")
      .set(field("password"), "?")
      .set(field("salt"), "?")
      .set(field("email"), "?")
      .set(field("role"), "?")
      .sql
    
    try {
      verticle.db.updateWithParamsAwait(query, parameters)
    } catch (e: Exception) {
      context.endAsErrorJson(500)
      return
    }
    
    context.endAsEmptyJson()
  }
}