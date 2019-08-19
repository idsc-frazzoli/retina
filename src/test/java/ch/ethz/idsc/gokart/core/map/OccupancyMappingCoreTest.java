// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.cache.CachedLog;
import ch.ethz.idsc.gokart.offline.map.OccupancyMappingOffline;
import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class OccupancyMappingCoreTest extends TestCase {
  public void testSimple() throws Exception {
    File file = CachedLog._20190701T174152_00.file();
    OccupancyMappingOffline occupancyMappingOffline = new OccupancyMappingOffline();
    OfflineLogPlayer.process(file, occupancyMappingOffline);
    BufferedImageRegion bufferedImageRegion = occupancyMappingOffline.erodedMap(2);
    BufferedImage bufferedImage = bufferedImageRegion.bufferedImage();
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_BYTE_GRAY);
    Tensor tensor = ImageFormat.from(bufferedImage);
    ScalarSummaryStatistics scalarSummaryStatistics = tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .collect(ScalarSummaryStatistics.collector());
    Scalar ratio = scalarSummaryStatistics.getAverage().divide(RealScalar.of(255.0));
    // System.out.println(ratio);
    Clips.interval(0.4, 0.5).requireInside(ratio);
    // ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("erodedmap.png"));
  }
}
