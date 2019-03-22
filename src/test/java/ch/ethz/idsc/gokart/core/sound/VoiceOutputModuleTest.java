// code by jph
package ch.ethz.idsc.gokart.core.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.TestCase;

public class VoiceOutputModuleTest extends TestCase {
  public void testAll() throws InterruptedException, LineUnavailableException, IOException, UnsupportedAudioFileException {
    VoiceOutputModule gokartVoiceOutputs = new VoiceOutputModule();
    gokartVoiceOutputs.first();
    for (int index = 0; index < 100; ++index)
      for (VoiceOutput voiceOutput : VoiceOutput.values()) {
        gokartVoiceOutputs.say(voiceOutput);
        Thread.sleep(10);
      }
    gokartVoiceOutputs.last();
  }
}
