package com.violetbeach.sentryproject

import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import org.springframework.stereotype.Component

@Component
class ErrorCodeBeforeSendCallback : SentryOptions.BeforeSendCallback {
    override fun execute(
        event: SentryEvent,
        hint: Hint,
    ): SentryEvent? {
        val exception = event.throwable
        if (exception is BaeminException) {
            event.setTag(ERROR_CODE_TAG, exception.code.name)
        }
        return event
    }
}

const val ERROR_CODE_TAG = "errorCode"
