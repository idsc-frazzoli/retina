// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamMappingStepUtil {
  ;
  /** update occurrence map with lidar ground truth
   * 
   * @param gokartLidarPose unitless representation
   * @param occurrenceMap
   * @param gokartFramePos [m] position of event in go kart frame */
  public static void updateOccurrenceMap(Tensor gokartPose, MapProvider occurrenceMap, double[] gokartFramePos) {
    GeometricLayer gokartToWorldLayer = GeometricLayer.of(Se2Utils.toSE2Matrix(gokartPose));
    Tensor worldCoord = gokartToWorldLayer.toVector(gokartFramePos[0], gokartFramePos[1]);
    occurrenceMap.addValue(worldCoord, 1);
  }
}
