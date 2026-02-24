# Add project specific ProGuard rules here.

# Keep Room entities
-keep class com.unstampedpages.app.data.local.entity.** { *; }

# Keep Gson models
-keep class com.unstampedpages.app.data.model.** { *; }

# NewRelic (for future integration)
# -keep class com.newrelic.** { *; }
