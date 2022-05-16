package ver14.Sound;


import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

/**
 * Sound - represents an audio clip that can be played on command.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Sound {
    /**
     * The constant soundEffectsPath.
     */
    private final static String ASSETS_SOUND_EFFECTS = "/assets/SoundEffects/";
    /**
     * The Manager.
     */
    private final SoundManager manager;
    /**
     * The Clip.
     */
    private Clip clip;

    /**
     * Instantiates a new Sound.
     *
     * @param relativePath the relative path
     * @param manager      the manager
     */
    public Sound(String relativePath, SoundManager manager) {
        this.manager = manager;
        if (!relativePath.endsWith(".wav")) {
            relativePath += ".wav";
        }
        relativePath = ASSETS_SOUND_EFFECTS + relativePath;
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(relativePath)));
            clip.addLineListener(event -> {
                //System.out.println("addLineListener-update: "+event.getType());
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.setMicrosecondPosition(0);
                }
            });

        } catch (Exception e) {

            clip = null;
            System.err.println("Error loading sound " + relativePath + "\n" + e);
        }
    }

    /**
     * Play forever (loop).
     */
    public void playLoop() {
        if (clip == null || !manager.isSoundEnabled())
            return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    /**
     * Stop playing.
     */
    public void stop() {
        if (clip == null)
            return;
        clip.stop();
    }

    /**
     * Play once.
     */
    public void play() {
        if (clip == null || !manager.isSoundEnabled())
            return;
        clip.start();
    }
}
