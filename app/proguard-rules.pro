# Snaptick R8 / ProGuard rules.
#
# Keep groups below cover every reflective code path in the app. If you add a
# library that uses reflection (Gson model, Room entity, Hilt module, JSON
# (de)serializer, BroadcastReceiver, AppWidget, etc.), extend the matching
# section instead of adding a one-off rule.
#
# When R8 strips something it shouldn't, you'll see a NoSuchMethodError or
# IllegalArgumentException at runtime. Re-add the keep rule and test again.

# ───────── Crash-trace readability ─────────
# Keep line numbers and source-file names so ACRA stack traces stay legible
# in release builds. Line numbers are not sensitive.
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ───────── Room ─────────
# Room generates DAO impls at compile time but reflectively probes entity
# field names at runtime via @ColumnInfo. Keeping entity classes + their
# fields prevents NPEs on first DB open.
-keep class com.vishal2376.snaptick.domain.model.** { *; }
-keep class com.vishal2376.snaptick.data.local.** { *; }
-keepclassmembers @androidx.room.Entity class * { *; }

# ───────── Gson ─────────
# Gson reads field names via reflection. Snaptick uses Gson for:
#   - util/BackupManager (BackupData, Task)
#   - widget/state/WidgetStateDefinition (WidgetState)
#   - util/GsonAdapters (LocalDate/LocalTime adapters)
-keep class com.vishal2376.snaptick.widget.model.** { *; }
-keepclassmembers class com.vishal2376.snaptick.domain.model.** { <fields>; }
-keepclassmembers class com.vishal2376.snaptick.widget.model.** { <fields>; }
-keepclassmembers,allowobfuscation class * { @com.google.gson.annotations.SerializedName <fields>; }
-keepattributes EnclosingMethod
-keep class com.google.gson.** { *; }

# ───────── Hilt / Dagger ─────────
# Hilt code-gen produces classes that are looked up reflectively at startup.
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }
-keep class **_HiltComponents { *; }
-keep class **_HiltComponents$* { *; }
-keep class * extends androidx.lifecycle.ViewModel
-keepclasseswithmembernames class * { @javax.inject.Inject <init>(...); }

# ───────── ACRA ─────────
# ACRA reflects across its own internals at crash time. If we strip its
# classes the report path will throw inside an already-broken app.
-keep class org.acra.** { *; }
-keepclassmembers class org.acra.** { *; }
-dontwarn org.acra.**

# ───────── Glance widget ─────────
# Glance's widget receiver + state serializer are loaded by the AppWidget
# framework via Class.forName indirection. Keep the widget tree.
-keep class com.vishal2376.snaptick.widget.** { *; }
-keep class androidx.glance.** { *; }
-dontwarn androidx.glance.**

# ───────── Compose ─────────
# Compose ships its own consumer rules in the BOM, but pin the safety nets.
-keepclassmembers class androidx.compose.** { *; }

# ───────── Kotlin coroutines ─────────
# Coroutine internals use reflection on volatile fields under debug agents.
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# ───────── BroadcastReceivers / WorkManager Workers ─────────
# These are instantiated by name by the Android framework + WorkManager.
-keep class com.vishal2376.snaptick.receiver.** { *; }
-keep class com.vishal2376.snaptick.worker.** { *; }
-keep class com.vishal2376.snaptick.widget.receiver.** { *; }
-keep class com.vishal2376.snaptick.widget.worker.** { *; }
-keep class com.vishal2376.snaptick.widget.action.** { *; }

# ───────── Material icons ─────────
# Some Material icon class names are referenced from Compose previews via
# reflection-style lookups. Cheap to keep.
-keep class androidx.compose.material.icons.** { *; }

# ───────── Compile-only / annotation-processor leakage ─────────
# Some AP-only classes (AutoService, javax.annotation.processing) reach the
# runtime classpath via transitive deps in Hilt / Dagger / kotlinx tools.
# Suppress R8 missing-class warnings for things only referenced in
# annotation-processor land.
-dontwarn javax.annotation.**
-dontwarn javax.annotation.processing.**
-dontwarn com.google.auto.service.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn org.checkerframework.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn lombok.**
-dontwarn com.sun.tools.javac.**
