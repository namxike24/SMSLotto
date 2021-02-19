#
## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile
#
#-printmapping mapping.txt
#-verbose
#-dontoptimize
#-dontpreverify
#-dontshrink
#-dontskipnonpubliclibraryclassmembers
#-dontusemixedcaseclassnames
#-keepparameternames
#-renamesourcefileattribute SourceFile
#-keepattributes *Annotation*
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
#
#-keep class * extends android.app.Activity
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#}
#
#
#-keep class com.facebook.** { *; }
#-keep class com.androidquery.** { *; }
#-keep class com.google.** { *; }
#-keep class org.acra.** { *; }
#-keep class org.apache.** { *; }
#-keep class com.mobileapptracker.** { *; }
#-keep class com.nostra13.** { *; }
#-keep class net.simonvt.** { *; }
#-keep class android.support.** { *; }
#-keep class com.nnacres.app.model.** { *; }
#-keep class com.facebook.** { *; }
#-keep class com.astuetz.** { *; }
#-keep class twitter4j.** { *; }
#-keep class com.actionbarsherlock.** { *; }
#-keep class com.dg.libs.** { *; }
#-keep class android.support.v4.** { *; }
#-keep class com.bluetapestudio.templateproject.** { *; }
#-keep class com.yourideatoreality.model.** { *; }
#-keep interface com.yourideatoreality.model.** { *; }
#-keep class com.bluetapestudio.** { *; }
#-keep interface com.bluetapestudio.** { *; }

### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class **$Properties { *; }
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**
-keep class org.greenrobot.greendao.**
-keep class com.smsanalytic.lotto.dao.**{*;}

-dontwarn org.greenrobot.greendao.**
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {

}
-keep class com.smsanalytic.lotto.entities.** { *; }
-keep class com.smsanalytic.lotto.database.** { *; }
-keep class com.firebase.** { *; }


-keepattributes *Annotation*
-keepclassmembers class * {
   @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep class org.jsoup.**
-keep class com.smsanalytic.lotto.common.** { *; }

-keepattributes Signature
-keep class com.smsanalytic.lotto.ui.register.AccountEntity { *; }

