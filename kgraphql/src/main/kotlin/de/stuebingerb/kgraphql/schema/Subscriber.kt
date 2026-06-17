package de.stuebingerb.kgraphql.schema

import kotlinx.serialization.json.Json

interface Subscriber {
    fun onSubscribe(subscription: Subscription)

    fun onNext(item: Any?)

    fun setArgs(args: Array<String>)

    fun onError(throwable: Throwable)

    fun onComplete()

    fun setJson(json: Json)
}
