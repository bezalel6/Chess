package ver14.SharedClasses.DBActions.DBRequest;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Login.AuthSettings;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * Pre made request - represents  a premade db request .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PreMadeRequest {

    /**
     * The constant TopPlayers.
     */
    public static final PreMadeRequest TopPlayers = new PreMadeRequest(RequestBuilder::top,
            AuthSettings.ANY_LOGIN,
            builder ->
                    new Variation("Top All Players", new Object[]{0}, new Arg[0]),
            builder -> new Variation("Top Five Players", new Object[]{5}, new Arg[0]));

    /**
     * The constant Games.
     */
    public static final PreMadeRequest Games = new PreMadeRequest(RequestBuilder::games, AuthSettings.USER, builder -> {
        Arg un = builder.args[0];
        return new Variation("All Games", new Object[]{un.repInStr, new Date(0), (Supplier<Date>) Date::new}, new Arg[]{un});
    }, builder -> {
        Arg un = builder.args[0];
        return new Variation("Games from last week", new Object[]{un.repInStr, (Supplier<Date>) () -> new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), (Supplier<Date>) Date::new}, new Arg[]{un});
    });
    /**
     * The constant DeleteUnfGames.
     */
    public static final PreMadeRequest DeleteUnfGames = new PreMadeRequest(RequestBuilder::deleteAllUnFinishedGames, AuthSettings.USER);
    /**
     * The constant StatsByTimeOfDay.
     */
    public static final PreMadeRequest StatsByTimeOfDay = new PreMadeRequest(RequestBuilder::statsByTimeOfDay, AuthSettings.USER);

    /**
     * The constant ChangeProfilePic.
     */
    public static final PreMadeRequest ChangeProfilePic = new PreMadeRequest(RequestBuilder::changeProfilePic, AuthSettings.USER);

    /**
     * The Statistics.
     */
    public final static PreMadeRequest[] statistics = {TopPlayers, Games, StatsByTimeOfDay};
    /**
     * The Auth settings.
     */
    public final @AuthSettings
    int authSettings;
    /**
     * The Builder builder.
     */
    private final Supplier<RequestBuilder> builderBuilder;
    /**
     * The Request variations.
     */
    private final PreMadeRequest[] requestVariations;

    /**
     * Instantiates a new Pre made request.
     *
     * @param builderBuilder the builder builder
     * @param authSettings   the auth settings
     * @param variations     the variations
     */
    PreMadeRequest(Supplier<RequestBuilder> builderBuilder, @AuthSettings int authSettings, VariationCreator... variations) {
        this.builderBuilder = builderBuilder;
        this.authSettings = authSettings;
        this.requestVariations = Arrays.stream(variations).map(variation -> new PreMadeRequest(() -> RequestBuilder.createVariation(builderBuilder, variation), authSettings)).toList().toArray(new PreMadeRequest[0]);
    }

    /**
     * Get request variations pre made request [ ].
     *
     * @return the pre made request [ ]
     */
    public PreMadeRequest[] getRequestVariations() {
        return requestVariations;
    }

    /**
     * Create builder request builder.
     *
     * @return the request builder
     */
    public RequestBuilder createBuilder() {
        return builderBuilder.get();
    }

    /**
     * Variation creator - represents a creator of a variation.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface VariationCreator {
        /**
         * Create variation.
         *
         * @param actualBuilder the actual builder
         * @return the variation
         */
        Variation create(RequestBuilder actualBuilder);
    }

    /**
     * Variation - represents a variation of a premade request.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class Variation {
        /**
         * The Variation name.
         */
        public final String variationName;
        /**
         * The Building args.
         */
        public final Object[] buildingArgs;
        /**
         * The Variation args.
         */
        public final Arg[] variationArgs;

        /**
         * Instantiates a new Variation.
         *
         * @param variationName the variation name
         * @param buildingArgs  the building args
         * @param variationArgs the variation args
         */
        Variation(String variationName, Object[] buildingArgs, Arg[] variationArgs) {
            this.variationName = variationName;
            this.buildingArgs = buildingArgs;
            this.variationArgs = variationArgs;
        }
    }
}
