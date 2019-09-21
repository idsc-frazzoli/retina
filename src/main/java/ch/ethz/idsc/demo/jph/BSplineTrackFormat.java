// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.TrackLane;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum BSplineTrackFormat {
  ;
  /** @param folder
   * @param bSplineTrack
   * @param resolution
   * @throws IOException */
  public static void write(File folder, BSplineTrack bSplineTrack, int resolution) throws IOException {
    if (folder.isFile())
      throw new RuntimeException();
    if (folder.isDirectory())
      DeleteDirectory.of(folder, 1, 5);
    folder.mkdir();
    // ---
    Export.of( //
        new File(folder, "controlpoints.csv"), //
        bSplineTrack.combinedControlPoints().map(Magnitude.METER).map(Round._3));
    {
      Tensor tensor = bSplineTrack.getLineMiddle(resolution);
      Export.of( //
          new File(folder, "middleLine.csv"), //
          tensor.map(Magnitude.METER).map(Round._3));
    }
    TrackLane trackLane = bSplineTrack.getTrackBoundaries(resolution);
    {
      Tensor tensor = Tensor.of(trackLane.leftBoundary().stream().map(PoseHelper::toUnitless)).map(Round._3);
      Export.of(new File(folder, "leftBoundary.csv"), tensor);
    }
    {
      Tensor tensor = Tensor.of(trackLane.rightBoundary().stream().map(PoseHelper::toUnitless)).map(Round._3);
      Export.of(new File(folder, "rightBoundary.csv"), tensor);
    }
  }
}
