package ver14.SharedClasses.Misc;/*
/**
 * Environment = utility class that stores some information about the current running environment
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */

public class Environment {

    /**
     * is this running from a jar or an ide. used for calculating paths.
     */
    public static final boolean IS_JAR = (Environment.class.getResource("Environment.class") + "").startsWith("jar");


}
