package com.mwhive.polygontesttask.data.remote.error

class CommonThrowable(
        message: String,
        val errorsMap: Map<String, String>? = null
) : Throwable(message)