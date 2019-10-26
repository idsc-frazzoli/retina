// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.TrackLane;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.BSplineTrackLcm;
import ch.ethz.idsc.gokart.offline.api.LastLogMessage;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Round;

/** functionality created for extracting last track until mpc starts of 6 GB log file
 * generated in experiments by mvb on 20190921 */
/* package */ enum BSplineTrackLogExtract {
  ;
  public static void main(String[] args) throws IOException {
    String channel = GokartLcmChannel.XYR_TRACK_CLOSED;
    Optional<ByteBuffer> optional = LastLogMessage.of( //
        new File("/media/datahaki/data/gokart/ultimate/20190921/20190921T124329_00", "log.lcm"), //
        channel);
    if (optional.isPresent()) {
      ByteBuffer byteBuffer = optional.get();
      BSplineTrack bSplineTrack = BSplineTrackLcm.decode(channel, byteBuffer).get();
      BSplineTrackLogExtract.write(HomeDirectory.file("20190921T124329_track"), bSplineTrack, 1000);
    } else {
      System.err.println("not found");
    }
  }

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
