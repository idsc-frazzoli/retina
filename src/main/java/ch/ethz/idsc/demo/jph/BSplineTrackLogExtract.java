// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.BSplineTrackLcm;
import ch.ethz.idsc.gokart.offline.api.LastLogMessage;
import ch.ethz.idsc.tensor.io.HomeDirectory;

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
      BSplineTrackFormat.write(HomeDirectory.file("20190921T124329_track"), bSplineTrack, 1000);
    } else {
      System.err.println("not found");
    }
  }
}
