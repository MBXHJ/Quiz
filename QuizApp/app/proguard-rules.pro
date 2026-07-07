# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.quizapp.data.db.entity.** { *; }

# Apache POI - keep all used classes
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.schemas.** { *; }
-keep class com.microsoft.schemas.** { *; }
-keep class com.fasterxml.jackson.** { *; }

# POI references desktop-Java classes not available on Android - harmless
-ignorewarnings
