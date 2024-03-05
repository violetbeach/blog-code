package com.violetbeach.sentryproject

import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import org.springframework.stereotype.Component

class CustomBeforeSendCallback : SentryOptions.BeforeSendCallback {
    override fun execute(
        event: SentryEvent,
        hint: Hint,
    ): SentryEvent? {
        event.exceptions
        if (hint["my-hint-key"] != null) {
            null
        }
        return event
    }
}
