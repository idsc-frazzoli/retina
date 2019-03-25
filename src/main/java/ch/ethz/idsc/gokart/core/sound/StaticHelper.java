// code by jph
package ch.ethz.idsc.gokart.core.sound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

/* package */ enum StaticHelper {
  ;
  static Clip play(AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
    AudioFormat audioFormat = audioInputStream.getFormat();
    DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
    Clip clip = (Clip) AudioSystem.getLine(info);
    clip.open(audioInputStream);
    clip.start();
    return clip;
  }
}
