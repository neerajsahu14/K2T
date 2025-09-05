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

# --- Firebase ---
# Keep data model classes used with Firebase (e.g., Firestore, Realtime Database).
# This prevents R8 from removing or renaming properties that are mapped via reflection.
# The `Food` model from your context will be covered by this rule.
-keep class com.app.k2t.firebase.model.** { *; }
-keepnames class com.app.k2t.firebase.model.**

# --- Jetpack Compose ---
# The following rules are recommended for Jetpack Compose to ensure that R8
# does not remove code that is used at runtime.
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keep class * implements androidx.compose.runtime.Composer {
    <init>(...);
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# --- Coil ---
# Coil's R8 rules are typically included automatically via its consumer rules.
# However, if you encounter issues with OkHttp (a dependency of Coil), you can add this:
-dontwarn okio.**