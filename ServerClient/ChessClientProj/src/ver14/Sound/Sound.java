package ver14.Sound;

/**
 * Sound - utility for playing music files.
 * 13/4/2021
 * By Ilan Peretz (ilanperets@gmail.com)
 */

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Sound {
    private final static String soundEffectsPath = "/assets/SoundEffects/";
    private final SoundManager manager;
    private Clip clip;

    public Sound(String relativePath, SoundManager manager) {
        this.manager = manager;
        if (!relativePath.endsWith(".wav")) {
            relativePath += ".wav";
        }
        relativePath = soundEffectsPath + relativePath;
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(relativePath)));
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    //System.out.println("addLineListener-update: "+event.getType());
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.setMicrosecondPosition(0);
                    }
                }
            });

        } catch (Exception e) {

            clip = null;
            System.err.println("Error loading sound " + relativePath + "\n" + e);
        }
    }

    // Play forver (loop)
    public void playLoop() {
        if (clip == null || !manager.isSoundEnabled())
            return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    // Stop play
    public void stop() {
        if (clip == null)
            return;
        clip.stop();
    }

    // Play only once
    public void play() {
        if (clip == null || !manager.isSoundEnabled())
            return;
        clip.start();
    }
}
