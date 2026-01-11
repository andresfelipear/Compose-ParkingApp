# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ==================== Retrofit ====================
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and). For Retrofit 2.9.0 or newer, you can use the following rule which preserves the names
# of HTTP method annotations used by Retrofit. This allows keeping of R8 in full mode while still
# allowing a useful stack trace.
-keepnames @kotlin.Metadata class * extends retrofit2.Callback

# ==================== OkHttp ====================
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ==================== Moshi ====================
# Keep Moshi annotations
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
}

# Keep @JsonClass annotated classes
-keep @com.squareup.moshi.JsonClass class * { *; }

# Keep generated JsonAdapter classes
-keepnames @com.squareup.moshi.JsonClass class *

# Moshi's Kotlin codegen
-keep class **JsonAdapter {
    <init>(...);
    <fields>;
}

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================== Hilt ====================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp

# ==================== Timber ====================
-dontwarn org.jetbrains.annotations.**

# ==================== Google Maps ====================
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# ==================== Coroutines ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== Keep DTOs and Entities ====================
-keep class com.aarevalo.parking.core.data.remote.dto.** { *; }
-keep class com.aarevalo.parking.core.data.local.entity.** { *; }
-keep class com.aarevalo.parking.core.domain.model.** { *; }
-keep class com.aarevalo.parking.authentication.domain.model.** { *; }
-keep class com.aarevalo.parking.map.domain.model.** { *; }
