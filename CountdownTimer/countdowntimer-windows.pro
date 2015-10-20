-injars       "C:\tmp\countdown\dst\countdown30.jar"
-outjars      "C:\tmp\countdown\dst\countdown30go.jar"
-libraryjars  "C:\Program Files (x86)\Java\jdk1.6.0_20\jre\lib\rt.jar"
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

