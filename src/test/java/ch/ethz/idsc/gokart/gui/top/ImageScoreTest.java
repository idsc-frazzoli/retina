// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.core.slam.SlamScore;
import junit.framework.TestCase;

public class ImageScoreTest extends TestCase {
  public void testSimple() {
    BufferedImage bufferedImage = PredefinedMap.DUBILAB_LOCALIZATION_20180901.getImage();
    SlamScore slamScore = ImageScore.of(bufferedImage);
    assertEquals(slamScore.evaluate(new Point2D.Double(-1, -1)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 124.9)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 125.5)), 255);
    assertEquals(slamScore.evaluate(new Point2D.Double(604.9, 125.5)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 126)), 0);
  }

  public void testSimple3() {
    BufferedImage bufferedImage = PredefinedMap.DUBILAB_LOCALIZATION_20180901.getImageExtruded();
    SlamScore slamScore = ImageScore.of(bufferedImage);
    assertEquals(slamScore.evaluate(new Point2D.Double(-1, -1)), 0);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 124.9)), 192);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 125.5)), 255);
    assertEquals(slamScore.evaluate(new Point2D.Double(604.9, 125.5)), 192);
    assertEquals(slamScore.evaluate(new Point2D.Double(605, 126)), 192);
  }
}
