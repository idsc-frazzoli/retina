// code by jph
package ch.ethz.idsc.gokart.offline.video;

import ch.ethz.idsc.gokart.core.pos.PoseLcmServerModule;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.Scalar;

public class TrackVideoConfig {
  /** maximum number of frames */
  public int frameLimit = Integer.MAX_VALUE;
  /** with unit s^-1 */
  public Scalar frameRate = PoseLcmServerModule.RATE;
  /** channel to extract pose from and trigger frame rendering */
  public String poseChannel = GokartPoseChannel.INSTANCE.channel();
  /** set lidarPoints to 0 to omit rendering */
  public int lidarPoints = 30_000;
}
