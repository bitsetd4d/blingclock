-injars       /tmp/countdown/dst/countdown30.jar
-outjars      /tmp/countdown/dst/countdown30go.jar
-libraryjars  /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/classes.jar
-libraryjars  /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/ui.jar
-printmapping proguard.map
-overloadaggressively
-defaultpackage ''
-allowaccessmodification
-dontskipnonpubliclibraryclasses

-keep public class countdowntimer.Main {
    public static void main(java.lang.String[]);
}
-keep public class countdowntimer.preferences.Preferences {
    public static Preferences getInstance();
}

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

