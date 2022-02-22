package ver6.server.game;

import ver6.SharedClasses.Callbacks.VoidCallback;
import ver6.SharedClasses.GameTime;
import ver6.SharedClasses.TimeFormat;

public class ServerGameTime extends GameTime {
    private final VoidCallback onTimeOut;

    public ServerGameTime(VoidCallback onTimeOut, TimeFormat... timeFormats) {
        super(timeFormats);
        this.onTimeOut = onTimeOut;
    }

    @Override
    public void timedOut() {
        onTimeOut.callback();
    }
}
