package com.wise.foundation.base

import java.math.BigDecimal
import java.util.*

private val RANDOM = Random()
/**
 * Created by jerry on 17-6-28.
 */

inline fun Any?.isNull(): Boolean {
    return null == this
}

inline fun Any?.isNotNull(): Boolean {
    return null != this
}

inline fun <T> T?.onNull(body: () -> Unit): T? {
    if (this.isNull()) body()
    return this
}

//fun <T> iif(bol:Boolean, t1:T?, t2:T?):T?{
//    return if(bol) t1 else t2
//}

inline fun <T> T?.onNullElse(body: () -> T): T {
    if (null == this) return body()
    return this
}

inline fun <T> T?.onNotNull(body: (T) -> Unit): T? {
    if (this.isNotNull()) body(this!!)
    return this
}


inline fun <T> T.listOf(): List<T> {
    return listOf(this)
}

inline fun <T> Collection<T>?.onNullEmpty(body: () -> Unit): Collection<T>? {
    if (null == this || this.isEmpty()) body()
    return this
}

inline fun <T> Collection<T>?.onNotNullEmpty(body: (Collection<T>) -> Unit): Collection<T>? {
    if (null != this && !this.isEmpty()) body(this)
    return this
}

inline fun Boolean.onTrue(body: (Boolean) -> Unit): Boolean {
    if (this) body(this)
    return this
}

inline fun Boolean.onFalse(body: (Boolean) -> Unit): Boolean {
    if (!this) body(this)
    return this
}

inline fun <T> T?.onNullOr(cdn: (T) -> Boolean, body: (T?) -> Unit): T? {
    if (null == this || cdn(this)) body(this)
    return this
}
//inline fun <T> T.on(cdn:(T)->Boolean, body: (T) -> Unit):T{
//    if(cdn(this)) body(this)
//    return this
//}
//inline fun <T> T?.onNull(body:()->T?):T?{
//    if(null == this) body()
//    return this
//}
//inline fun <T> T?.onNotNull(body: () -> T?): T? {
//    if(null != this) body()
//    return this
//}
//inline fun <T> Array<T>?.on(cdn:(Array<T>?)->Boolean, body: (Array<T>?) -> List<T>):List<T>{
//    if(cdn(this)) body(this)
//    if(null==this) return emptyList()
//    return listOf(*this)
//}

fun <T> Array<T>?.randomEl(): T? {
    if (null == this) return null
    return this[randomIn(0, this.size - 1)]
}

fun <T> Array<T?>?.randomEle(): T? {
    if (null == this) return null
    return this[randomIn(0, this.size - 1)]
}

fun <T> List<T>?.randomEl(): T? {
    if (null == this) return null
    return this[randomIn(0, this.size - 1)]
}

fun <T> List<T?>?.randomEle(): T? {
    if (null == this) return null
    return this[randomIn(0, this.size - 1)]
}

fun randomIn(min: Int, max: Int): Int {
    return min + RANDOM.nextInt((max - min) + 1)
}

fun randomIn(min: Long, max: Long): Long {
    var v = max - min
    if (v <= Int.MIN_VALUE || v >= Int.MAX_VALUE) {
        v = (Int.MAX_VALUE - 1).toLong()
    }
    return min + RANDOM.nextInt(v.toInt())
}

fun Int.toBigDecimal(): BigDecimal {
    return BigDecimal(this)
}

fun Long.toBigDecimal(): BigDecimal {
    return BigDecimal(this)
}

fun Double.toBigDecimal(): BigDecimal {
    return BigDecimal(this)
}

fun Double.toBigDecimal(score: Int): BigDecimal {
    return BigDecimal(this).setScale(score, BigDecimal.ROUND_HALF_UP)
}
