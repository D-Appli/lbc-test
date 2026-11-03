package fr.leboncoin.core.kotlin.mapper

interface Mapper<T, R> {
    fun map(param: T): R
}
