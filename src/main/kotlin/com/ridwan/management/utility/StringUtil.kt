package com.ridwan.management.utility

import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets
import java.security.SecureRandom


private val random = SecureRandom()

fun generateRandomString(length: Int): String {
  val leftLimit = 48
  val rightLimit = 123
  val builder = StringBuilder()
  
  random.ints(leftLimit, rightLimit)
    .limit(length.toLong())
    .forEach { builder.append(it.toChar()) }
  
  return builder.toString()
}

fun hashPassword(password: String, salt: String): String {
  return Hashing.sha512()
    .hashString(salt + password, StandardCharsets.UTF_8)
    .toString()
}