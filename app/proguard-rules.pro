# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ---------------------------------------------------------------------------
# Moshi (Moshi codegen/reflection keeps MUST come first so JSON adapters,
# generated JsonAdapters and reflective model field names resolve correctly)
# ---------------------------------------------------------------------------

# Moshi @JsonClass annotated classes (codegen adapters + reflective lookup)
-keep @com.squareup.moshi.JsonClass class *;

# App data models deserialized via Moshi (JSON field-name stability)
-keep class com.example.model.** { *; }

# Room entities (also referenced by Moshi/JSON + must keep column/field names)
-keep class com.example.data.local.** { *; }

# Preserve members of classes/members annotated with @JsonClass or @Keep
# (if present) so Moshi and legacy reflective access keep working.
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <methods>;
}
-keep @androidx.annotation.Keep class * { *; }

# Classes exposing @FromJson/@ToJson converter methods
-keepclasseswithmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# ---------------------------------------------------------------------------
# Retrofit (service interfaces and Response are looked up reflectively)
# ---------------------------------------------------------------------------
-keep,allowobfuscation,allowshrinking interface retrofit2.Call;
-keep,allowobfuscation,allowshrinking class retrofit2.Response;

# ---------------------------------------------------------------------------
# OkHttp / Okio (suppress missing-class warnings for optional/transitive deps)
# ---------------------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**

# ---------------------------------------------------------------------------
# Appwrite SDK (used reflectively by the client for requests/serialization)
# ---------------------------------------------------------------------------
-keep class io.appwrite.** { *; }
-dontwarn io.appwrite.**

# ---------------------------------------------------------------------------
# kotlinx / kotlin metadata (silence warnings from optional multiplatform
# metadata that is not packaged in the Android artifact)
# ---------------------------------------------------------------------------
-dontwarn kotlinx.**
-dontwarn kotlin.**

# ---------------------------------------------------------------------------
# BuildConfig (referenced by string constant lookups / secrets plugin)
# ---------------------------------------------------------------------------
-keep class com.aistudio.ebarangaysua.suaxyz.BuildConfig { *; }
