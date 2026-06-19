# AIIC ProGuard Rules — Production Security

# ──────────────────────────────────────────
# GENERAL
# ──────────────────────────────────────────
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
-keep public class * extends java.lang.Exception
-renamesourcefileattribute SourceFile

# ──────────────────────────────────────────
# HILT / DAGGER
# ──────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# ──────────────────────────────────────────
# COROUTINES
# ──────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ──────────────────────────────────────────
# ROOM (future)
# ──────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ──────────────────────────────────────────
# COMPOSE
# ──────────────────────────────────────────
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ──────────────────────────────────────────
# DATASTORE
# ──────────────────────────────────────────
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# ──────────────────────────────────────────
# DOMAIN MODELS — Never obfuscate
# ──────────────────────────────────────────
-keep class com.aiic.app.domain.model.** { *; }
-keep class com.aiic.app.data.model.** { *; }

# ──────────────────────────────────────────
# KOTLIN SERIALIZATION (future)
# ──────────────────────────────────────────
-keepattributes RuntimeVisibleAnnotations
-keep class kotlin.Metadata { *; }

# ──────────────────────────────────────────
# SECURITY: Remove logging in release
# ──────────────────────────────────────────
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
