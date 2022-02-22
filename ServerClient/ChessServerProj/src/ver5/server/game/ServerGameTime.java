package ver5.server.game;

import ver5.SharedClasses.Callback;
import ver5.SharedClasses.GameTime;
import ver5.SharedClasses.TimeFormat;

public class ServerGameTime extends GameTime {
    private final Callback onTimeOut;

    public ServerGameTime(Callback onTimeOut, TimeFormat... timeFormats) {
        super(timeFormats);
        this.onTimeOut = onTimeOut;
    }

    @Override
    public void timedOut() {
        onTimeOut.callback();
    }
}
