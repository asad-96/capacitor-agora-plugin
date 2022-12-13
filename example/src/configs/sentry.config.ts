export const sentryConfig = (env) => ({
  dsn: env.SENTRY_DSN,
  integrations: [],
  // Set tracesSampleRate to 1.0 to capture 100%
  // of transactions for performance monitoring.
  // We recommend adjusting this value in production
  tracesSampleRate: 1.0,
  config: {
    beforeSend(event) {
      const data = JSON.stringify(event.request)
      event.request.debug_text = data
      return event
    }
  }
})
