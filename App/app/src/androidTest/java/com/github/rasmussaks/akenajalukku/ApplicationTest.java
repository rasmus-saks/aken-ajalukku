package com.github.rasmussaks.akenajalukku;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    private static final String APP_PACKAGE = "com.github.rasmussaks.akenajalukku.debug";
    private UiDevice device;

    private void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            device.wait(Until.hasObject(By.text("Allow")), 10000);
            UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    Log.e("aken-ajalukku", "No allow permissions button to interact with", e);
                }
            }
        }
    }

    @Before
    public void startMainActivityFromHomeScreen() {

        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        assertTrue("Failed to find launcher", device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 30000));
        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        allowPermissionsIfNeeded();
        // Wait for the app to appear
        assertFalse("Google Play services is out of date", device.wait(Until.hasObject(By.textContains("unless you update Google Play services")), 5000));

        assertTrue("Failed to find map", device.wait(Until.hasObject(By.res(APP_PACKAGE, "map")), 30000));
    }

    @Test
    public void testMarker() throws Exception {
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Raekoja plats"));
        marker.click();
        assertEquals("Testing test : ", 2 + 2, 4);
    }
}