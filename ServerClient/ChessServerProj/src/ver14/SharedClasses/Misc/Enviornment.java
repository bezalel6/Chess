package ver14.SharedClasses.Misc;/*
/**
 *utility class that stores some information about the current running environment
 */

public class Enviornment {

    /**
     * is this running from a jar or an ide. used for calculating paths.
     */
    public static final boolean IS_JAR = (Enviornment.class.getResource("Enviornment.class") + "").startsWith("jar");


}
