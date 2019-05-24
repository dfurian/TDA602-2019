package the.least.permissions;

import java.util.Arrays;
import java.util.List;

public class InstalledAppUtils {

    static final String APP_TWITTER = "com.twitter.android";
    static final String APP_TWITTER_LITE = "com.twitter.android.lite";
    static final String APP_INSTAGRAM = "com.instagram.android";
    static final List<String> APPS_SOCIAL = Arrays.asList(APP_TWITTER, APP_TWITTER_LITE, APP_INSTAGRAM);

    static final String APP_GRINDR = "com.grindrapp.android";
    static final String APP_HORNET = "com.hornet.android";
    static final String APP_HER = "com.weareher.her";
    static final List<String> APPS_LGBT = Arrays.asList(APP_GRINDR, APP_HORNET, APP_HER);

    static final String APP_MYSUGR = "com.mysugr.android.companion";
    static final String APP_DIABETES_M = "com.mydiabetes";
    static final List<String> APPS_DIABETES = Arrays.asList(APP_MYSUGR, APP_DIABETES_M);

    static final String APP_PREGLIFE = "com.gravid.gravid";
    static final List<String> APPS_FEMALE = Arrays.asList(APP_HER, APP_PREGLIFE);

    static final String APP_BLOOD_PRESSURE_DIARY = "com.bluefish.bloodpressure";
    static final String APP_CALORIE_COUNTER = "com.myfitnesspal.android";
    static final List<String> APPS_HEALTH = Arrays.asList(APP_BLOOD_PRESSURE_DIARY, APP_CALORIE_COUNTER);

    static Boolean isSocial(String appName) {
        return appName != null && APPS_SOCIAL.contains(appName);
    }

    static Boolean isLgbt(String appName) {
        return appName != null && APPS_LGBT.contains(appName);
    }

    static Boolean isDiabetes(String appName) {
        return appName != null && APPS_DIABETES.contains(appName);
    }

    static Boolean isFemale(String appName) {
        return appName != null && APPS_FEMALE.contains(appName);
    }

    static Boolean isHealth(String appName) {
        return appName != null && APPS_HEALTH.contains(appName);
    }
}
