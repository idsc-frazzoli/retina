// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class TrackVideoWriterTest extends TestCase {
  public void testSimple() throws Exception {
    File file = new File("src/test/resources/localization", "vlp16.center.pos.lcm");
    assertTrue(file.isFile());
    File target = HomeDirectory.file(getClass().getSimpleName() + ".mp4");
    assertFalse(target.exists());
    try (TrackVideoWriter trackVideoRender = new TrackVideoWriter( //
        new VideoBackground(new BufferedImage(1920, 1280, BufferedImage.TYPE_3BYTE_BGR), IdentityMatrix.of(3)), //
        new TrackVideoConfig(), //
        target)) {
      OfflineLogPlayer.process(file, trackVideoRender);
    }
    assertTrue(target.exists());
    target.delete();
    assertFalse(target.exists());
  }
}
