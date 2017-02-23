# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# 指定代码的压缩级别
-optimizationpasses 5
# 混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames
# 是否混淆第三方jar
-dontskipnonpubliclibraryclasses
# 混淆时是否做预校验
-dontpreverify
# 混淆时是否记录日志
-verbose
# 不优化输入的类文件
#-dontoptimize
# 不压缩输入的类文件
#-dontshrink
# 不混淆输入的类文件
#-dontobfuscate
# 代码混淆采用的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

-keepattributes *Annotation*
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# 忽略 自定义实体类
-keep class player.sdk.bean.** {*;}

# 忽略 第三方jar
# 支付宝sdk
-keep class com.alipay.** {*;}
-keep class com.ta.utdid2.** {*;}
-keep class com.ut.device.** {*;}
-keep class org.json.alipay.** {*;}
-dontwarn com.alipay.**
-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-dontwarn org.json.alipay.**
# 微信sdkz
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.yunke.android.wxapi.**{
*;
}
# gson
-keep class com.google.gson.** {*;}
-dontwarn com.google.gson.**
# ButterKnif
-keep class butterknife.** {*;}
-keep class butterknife.internal.** {*;}
-keep class **$$ViewInjector {*;}
-keep class **$$ViewBinder {*;}
-keep class **$$Finder {*;}
-keep class **$$Injector {*;}
-keep class **$$Action {*;}
-keep class **$$Setter {*;}
-dontwarn butterknife.**
-dontwarn butterknife.internal.**
-keepnames class * {
    @butterknife.Bind *;
}
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
# pinyin
-keep class opensource.jpinyin.** {*;}
-dontwarn opensource.jpinyin.**
# android
#-keep class android.support.** {*;}
#-keep interface android.support.** {*;}
-keep class android.annotation.** {*;}
-keep public class android.webkit.**
#-dontwarn android.support.**
-dontwarn android.annotation.**
-dontwarn android.webkit.**
# javax
-keep public class javax.**
-dontwarn javax.**
# 友盟SocialSdk
-keep public class com.yunke.android.R$*{
    public static final int *;
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-dontwarn com.umeng.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
# AutobahnAndroid
-keep class de.tavendo.autobahn.** {*;}
-keep class org.codehaus.jackson.** {*;}
-dontwarn de.tavendo.autobahn.**
-dontwarn org.codehaus.jackson.**
# async-http
-keep class com.loopj.android.http.** {*;}
-dontwarn com.loopj.android.http.**
# httpcore
-keep class org.apache.http.** {*;}
-dontwarn org.apache.http.**
# uk-co-senab-photoview
-keep class uk.co.senab.photoview.** {*;}
-dontwarn uk.co.senab.photoview.**
# ijkplayer
-keep class tv.danmaku.ijk.** { *;}
-dontwarn tv.danmaku.ijk.**
# SayHi
-keep class player.sdk.util.SayHi {*;}
# okio
-keep public class org.codehaus.**
-keep public class java.nio.**
-dontwarn org.codehaus
-dontwarn java.nio
# YouMeng_Push
-dontwarn com.ut.mini.**
-dontwarn okio.**
-dontwarn com.xiaomi.**
-dontwarn com.squareup.wire.**
-dontwarn android.support.v4.**

-keepattributes *Annotation*

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class okio.** {*;}
-keep class com.squareup.wire.** {*;}

-keep class com.umeng.message.protobuffer.* {
	 public <fields>;
         public <methods>;
}

-keep class com.umeng.message.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.impl.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}

-keep public class **.R$*{
    public static final int *;
}
#如果compileSdkVersion为23，请添加以下混淆代码
-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }


#联通支付
#-libraryjars  libs/Multimode_UniPay_base.jar
#-libraryjars  libs/miniapay.jar
#-libraryjars  libs/alipaysdk.jar
#-libraryjars  libs/MobileSecSdk.jar
#-libraryjars  libs/utdid4all-1.0.4.jar

-keep class com.unipay.account.**{*;}
-keep class com.unicom.dcLoader.**{*;}
-keep class com.unicom.wostore.unipay.paysecurity.**{*;}
-keep class com.wow.shell.**{*;}

-keep class cn.egame.terminal.miniapay.**{*;}
-keep class egame.terminal.feesmslib.jni.**{*;}

-keep class com.alipay.sdk.**{*;}
-keep class com.alipay.auth.**{*;}
-keep class com.alipay.android.app.**{*;}
