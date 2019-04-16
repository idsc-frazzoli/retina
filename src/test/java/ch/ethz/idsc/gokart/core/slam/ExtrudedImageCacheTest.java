// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;
import java.io.File;

import junit.framework.TestCase;

public class ExtrudedImageCacheTest extends TestCase {
  public void testSimple() {
    String title = getClass().getSimpleName();
    File file = new File(ExtrudedImageCache.FOLDER, title + ".png");
    assertFalse(file.exists());
    BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);//
    BufferedImage bi2 = ExtrudedImageCache.of(title, () -> bufferedImage);
    assertTrue(file.isFile());
    file.delete();
    assertFalse(file.exists());
    assertEquals(bi2, bufferedImage);
  }
}
