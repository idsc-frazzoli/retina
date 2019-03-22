// code by jph
package ch.ethz.idsc.gokart.core.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.ethz.idsc.demo.travis.TravisUserName;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class VoiceOutputModuleTest extends TestCase {
  public void testAll() throws InterruptedException, LineUnavailableException, IOException, UnsupportedAudioFileException {
    if (!TravisUserName.whoami()) {
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

  public void testResources() {
    for (VoiceOutput voiceOutput : VoiceOutput.values()) {
      assertNotNull(ResourceData.class.getResourceAsStream(voiceOutput.resource()));
      // voiceOutput.resource();
    }
  }
}
