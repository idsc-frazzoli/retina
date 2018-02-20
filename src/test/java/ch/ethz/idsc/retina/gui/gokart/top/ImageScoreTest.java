// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.slam.SlamScore;
import junit.framework.TestCase;

public class ImageScoreTest extends TestCase {
  public void testSimple() {
    // /map/dubendorf/hangar/20180122.png
    BufferedImage bufferedImage = StoreMapUtil.loadOrNull();
    SlamScore slamScore = ImageScore.of(bufferedImage);
    assertEquals(slamScore.evaluate(new Point2D.Double(-1, -1)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 124.9)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 125.5)), 255);
    assertEquals(slamScore.evaluate(new Point2D.Double(604.9, 125.5)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 126)), 0);
  }
}
