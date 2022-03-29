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

#视频播放器gsyvideoplayer
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#阿里Arouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

#高德地图
#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}
#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#搜索
-keep   class com.amap.api.services.**{*;}
##2D地图
#-keep class com.amap.api.maps2d.**{*;}
#-keep class com.amap.api.mapcore2d.**{*;}
##导航
#-keep class com.amap.api.navi.**{*;}
#-keep class com.autonavi.**{*;}

#优化
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护内部类
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep class com.bytedance.sdk.openadsdk.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}


#------app混淆配置------#
#-printmapping mapping.txt
-obfuscationdictionary ./dictionary.txt         #引用第三方字典
-classobfuscationdictionary ./dictionary.txt    #引用第三方字典
-packageobfuscationdictionary ./dictionary.txt  #引用第三方字典
-keepattributes SourceFile,LineNumberTable      #保留行号
-keep class com.tencent.**{*;}
-keep class android.**{*;}
-keep class androidx.**{*;}
-keep class org.** {*;}
-keep class com.yang.lib_common.bus.event.LiveDataBus.**{*;}
-dontusemixedcaseclassnames                     # 是否使用大小写混合
-verbose                                        # 混淆时是否记录日志
-optimizationpasses 5                           # 指定代码的压缩级别
-keepattributes *Annotation*                    #保护注解
-keep class * implements java.io.Serializable {*;}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
#不混淆自定义注解生成的类
-keep class * implements com.yang.apt_processor.manager.InjectManager{*;}
#不混淆ViewModel
-keep class **.*ViewModel{*;}
#不混淆dagger2 Component
-keep class **.*Component{*;}
#不混淆Fragment
-keep class **.*Fragment{*;}
