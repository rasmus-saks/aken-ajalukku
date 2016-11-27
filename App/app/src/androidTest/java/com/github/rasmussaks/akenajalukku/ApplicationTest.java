package com.github.rasmussaks.akenajalukku;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.Journey;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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
            device.wait(Until.hasObject(By.res("com.android.packageinstaller", "permission_allow_button")), 10000);
            UiObject2 allowPermissions = device.findObject(By.res("com.android.packageinstaller", "permission_allow_button"));
            if (allowPermissions != null) {
                allowPermissions.click();
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
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000);
        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        allowPermissionsIfNeeded();
        // Wait for the app to appear

        assertTrue("Failed to find map", device.wait(Until.hasObject(By.res(APP_PACKAGE, "map")), 30000));
    }

    @Test
    public void testMarker() throws Exception {
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Raekoja plats"));
        marker.click();
    }

    @Test
    public void openSettingsMenu() throws Exception {
        UiObject2 setButton = device.findObject(By.res(APP_PACKAGE, "settings_button"));
        setButton.click();
        assertTrue("Failed to open settings menu", device.wait(Until.hasObject(By.text("Settings")), 20000));
    }

    @Test
    public void testMarkerDetails() throws Exception {
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Raekoja plats"));
        marker.click();
        assertTrue("Failed to find the marker detailed view", device.wait(Until.hasObject(By.res(APP_PACKAGE, "the_story")), 10000));
    }

    @Test
    public void openJourneyList() throws Exception {
        UiObject2 button = device.findObject(By.res(APP_PACKAGE, "journey_button"));
        button.click();
        assertTrue("Failed to find the journey list", device.wait(Until.hasObject(By.clazz("android.widget.ListView")), 10000));

    }
    @Test
    public void parcelTest() throws Exception {
        Data testimiseks = new Data(false);
        PointOfInterest uus = new PointOfInterest(-5, new LatLng(755445,1222123), "näideUnicode",
                "Lihtne näide, et kontrollida täpitähti", "http://i.imgur.com/CyDodpSr.jpg",
                "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/x264_aac_faac.mp4");
        PointOfInterest teine = new PointOfInterest(-6, new LatLng(755445,1222123), "näideUnicode",
                "Lihtne näide, et kontrollida täpitähti", "http://i.imgur.com/CyDodpSr.jpg",
                "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/x264_aac_faac.mp4");
        ArrayList<Integer> pois = new ArrayList<>();
        pois.add(-5);
        pois.add(-6);
        ArrayList<PointOfInterest> nimekiri = new ArrayList<>();
        nimekiri.add(uus);
        nimekiri.add(teine);
        Journey teekond = new Journey(-4, pois, "Ameerika mäed", "Kohapealne menüü", nimekiri);
        ArrayList<Journey> konnad = new ArrayList<>();
        konnad.add(teekond);
        testimiseks.setPois(nimekiri);
        testimiseks.setJourneys(konnad);
        Parcel against = Parcel.obtain();
        testimiseks.writeToParcel(against, 0);
        against.setDataPosition(0);
        Data vastus = Data.CREATOR.createFromParcel(against);
        PointOfInterest Epoi = vastus.getPoiById(-5);
        PointOfInterest Tpoi = testimiseks.getPoiById(-5);
        assertEquals(Epoi.getId(), Tpoi.getId());
        assertEquals(Epoi.getDescription(), Tpoi.getDescription());
        assertEquals(Epoi.getImageUrl(), Tpoi.getImageUrl());
        assertEquals(Epoi.getLocation(), Tpoi.getLocation());
        assertEquals(Epoi.getMarker(), Tpoi.getMarker());
        Journey Konn = testimiseks.getJourneyById(-4);
        assertEquals(Konn.getPoiIdList(), teekond.getPoiIdList());
        assertEquals(Konn.getFirstPoi(), teekond.getFirstPoi());
    }
}