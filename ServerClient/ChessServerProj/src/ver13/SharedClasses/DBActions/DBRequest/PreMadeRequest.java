package ver13.SharedClasses.DBActions.DBRequest;

import ver13.SharedClasses.AuthSettings;
import ver13.SharedClasses.Callbacks.ObjCallback;
import ver13.SharedClasses.DBActions.Arg.Arg;
import ver13.SharedClasses.DBActions.RequestBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PreMadeRequest {
    public static final PreMadeRequest select = new PreMadeRequest(RequestBuilder::select, AuthSettings.NO_AUTH);

    public static final PreMadeRequest TopPlayers = new PreMadeRequest(RequestBuilder::top, AuthSettings.ANY_LOGIN, builder -> new Variation("Top Five Players", new Object[]{5}, new Arg[0]));

    public static final PreMadeRequest Games = new PreMadeRequest(RequestBuilder::games, AuthSettings.USER, builder -> {
        Arg un = builder.args[0];
        return new Variation("All Games", new Object[]{un.repInStr, new Date(0), new Date()}, new Arg[]{un});
    }, builder -> {
        Arg un = builder.args[0];
        return new Variation("Games from last week", new Object[]{un.repInStr, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), new Date()}, new Arg[]{un});
    });
    public final static PreMadeRequest[] statistics = {TopPlayers, Games, select};
    public final @AuthSettings
    int authSettings;
    private final ObjCallback<RequestBuilder> builderBuilder;
    private final PreMadeRequest[] requestVariations;

    PreMadeRequest(ObjCallback<RequestBuilder> builderBuilder, @AuthSettings int authSettings, VariationCreator... variations) {
        this.builderBuilder = builderBuilder;
        this.authSettings = authSettings;
        this.requestVariations = Arrays.stream(variations).map(var -> new PreMadeRequest(() -> RequestBuilder.createVariation(builderBuilder, var), authSettings)).toList().toArray(new PreMadeRequest[0]);
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