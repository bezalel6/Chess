package ver14.SharedClasses.DBActions.DBRequest;

import ver14.SharedClasses.Callbacks.ObjCallback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Login.AuthSettings;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class PreMadeRequest {

    public static final PreMadeRequest TopPlayers = new PreMadeRequest(RequestBuilder::top,
            AuthSettings.ANY_LOGIN,
            builder ->
                    new Variation("Top All Players", new Object[]{0}, new Arg[0]),
            builder -> new Variation("Top Five Players", new Object[]{5}, new Arg[0]));

    public static final PreMadeRequest Games = new PreMadeRequest(RequestBuilder::games, AuthSettings.USER, builder -> {
        Arg un = builder.args[0];
        return new Variation("All Games", new Object[]{un.repInStr, new Date(0), (ObjCallback<Date>) Date::new}, new Arg[]{un});
    }, builder -> {
        Arg un = builder.args[0];
        return new Variation("Games from last week", new Object[]{un.repInStr, (ObjCallback<Date>) () -> new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), (ObjCallback<Date>) Date::new}, new Arg[]{un});
    });
    public static final PreMadeRequest DeleteUnfGames = new PreMadeRequest(RequestBuilder::deleteAllUnFinishedGames, AuthSettings.USER);
    public static final PreMadeRequest StatsByTimeOfDay = new PreMadeRequest(RequestBuilder::statsByTimeOfDay, AuthSettings.USER);

    public static final PreMadeRequest ChangeProfilePic = new PreMadeRequest(RequestBuilder::changeProfilePic, AuthSettings.USER);

    public final static PreMadeRequest[] statistics = {TopPlayers, Games, StatsByTimeOfDay};
    public final @AuthSettings
    int authSettings;
    private final ObjCallback<RequestBuilder> builderBuilder;
    private final PreMadeRequest[] requestVariations;

    PreMadeRequest(ObjCallback<RequestBuilder> builderBuilder, @AuthSettings int authSettings, VariationCreator... variations) {
        this.builderBuilder = builderBuilder;
        this.authSettings = authSettings;
        this.requestVariations = Arrays.stream(variations).map(variation -> new PreMadeRequest(() -> RequestBuilder.createVariation(builderBuilder, variation), authSettings)).toList().toArray(new PreMadeRequest[0]);
    }

    public PreMadeRequest[] getRequestVariations() {
        return requestVariations;
    }

    public RequestBuilder createBuilder() {
        return builderBuilder.get();
    }

    public interface VariationCreator {
        Variation create(RequestBuilder actualBuilder);
    }

    public static class Variation {
        public final String variationName;
        public final Object[] buildingArgs;
        public final Arg[] variationArgs;

        Variation(String variationName, Object[] buildingArgs, Arg[] variationArgs) {
            this.variationName = variationName;
            this.buildingArgs = buildingArgs;
            this.variationArgs = variationArgs;
        }
    }
}
