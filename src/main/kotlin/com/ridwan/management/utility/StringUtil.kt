package com.ridwan.management.utility

import java.security.SecureRandom

private val random = SecureRandom()

fun generateRandomString(length: Int): String {
    val leftLimit = 48
    val rightLimit = 123
    val builder = StringBuilder()

    val result = random.ints(leftLimit, rightLimit)
            .limit(length.toLong())
            .forEach { builder.append(it.toChar()) }

    return result.toString()
}