// code by jph
package ch.ethz.idsc.retina.util.io;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class PngAnimationWriterTest extends TestCase {
  public void testSimple() throws Exception {
    File directory = HomeDirectory.Pictures(PngAnimationWriter.class.getSimpleName());
    assertFalse(directory.exists());
    try (AnimationWriter animationWriter = new PngAnimationWriter(directory)) {
      animationWriter.write(new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY));
      animationWriter.write(new BufferedImage(3, 3, BufferedImage.TYPE_BYTE_GRAY));
    }
    assertTrue(directory.isDirectory());
    DeleteDirectory deleteDirectory = DeleteDirectory.of(directory, 1, 3);
    assertEquals(deleteDirectory.deletedCount(), 3);
    assertFalse(directory.exists());
  }
}
