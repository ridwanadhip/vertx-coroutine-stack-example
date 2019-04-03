package com.ridwan.management.extension

import org.jooq.Record
import org.jooq.SelectLimitPercentStep
import org.jooq.SelectLimitStep

fun <R: Record> SelectLimitStep<R>.limit(value: String): SelectLimitPercentStep<R> {
  return this.limit(0)
}