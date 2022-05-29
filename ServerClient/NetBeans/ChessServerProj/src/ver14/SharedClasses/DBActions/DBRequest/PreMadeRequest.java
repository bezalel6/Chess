package ver14.SharedClasses.DBActions.DBRequest;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Login.AuthSettings;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * represents a db request with a {@link Supplier<RequestBuilder>} able to generate unique request builders.
 * a remade request also has an {@link AuthSettings} for limiting access to the requests.
 * some {@link PreMadeRequest}s might want to provide some variations of an existing {@link PreMadeRequest},
 * and may implement the {@link VariationCreator} interface.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 * @see Variation
 * @see VariationCreator
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
     * @param builderBuilder the supplier of builders
     * @param authSettings   the auth requirements for this request
     * @param variations     the variations for this request
     */
    PreMadeRequest(Supplier<RequestBuilder> builderBuilder, @AuthSettings int authSettings, VariationCreator... variations) {
        this.builderBuilder = builderBuilder;
        this.authSettings = authSettings;
        this.requestVariations = Arrays.stream(variations).map(variation -> new PreMadeRequest(() -> RequestBuilder.createVariation(builderBuilder, variation), authSettings)).toList().toArray(new PreMadeRequest[0]);
    }

    /**
     * Get the variations for this request
     *
     * @return the variations for this request
     */
    public PreMadeRequest[] getRequestVariations() {
        return requestVariations;
    }

    /**
     * Create a new unique request builder
     *
     * @return the request builder
     */
    public RequestBuilder createBuilder() {
        return builderBuilder.get();
    }

}
