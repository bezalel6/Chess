package ver14.Tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.Location;

import java.util.Locale;

@Test(testName = "Test Locs")
public class LocationTests {


    @Test(testName = "test string generation matches location obj")
    public static void checkStrGen() {
        forEachLoc(loc -> {
            Assert.assertEquals(loc.toString(), loc.name().toLowerCase(Locale.ROOT));
            Assert.assertEquals(Location.getLoc(loc.toString()), loc);
        });
    }

    private static void forEachLoc(Callback<Location> callback) {
        Location.ALL_LOCS.forEach(callback::callback);
    }

    @Test(testName = "checks all locs are inside bounds")
    public static void bounds() {
        forEachLoc(loc -> {
            Assert.assertTrue(Location.isInBounds(loc));
        });
    }

    private void checkCLrs() {

    }


}
