// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobs;
import ch.ethz.idsc.gokart.lcm.MessageConsistency;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import idsc.BinaryBlob;
import lcm.lcm.LCMDataOutputStream;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

/** changes pose messages based on given pose estimating interface */
public enum LogPoseInject {
  ;
  public static void process(File src, File dst, OfflinePoseEstimator offlinePoseEstimator) throws Exception {
    if (dst.exists())
      throw new RuntimeException();
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    Set<String> set = new HashSet<>();
    Long tic = null;
    System.out.println("start");
    try {
      while (true) {
        // TODO code redundant to OfflineLogPlayer
        Event event = log.readNext();
        // System.out.println("here");
        {
          if (Objects.isNull(tic))
            tic = event.utime;
          BinaryBlob binaryBlob = null;
          try {
            binaryBlob = new BinaryBlob(event.data);
          } catch (Exception exception) {
            if (set.add(event.channel))
              System.err.println("not a binary blob: " + event.channel);
          }
          if (Objects.nonNull(binaryBlob)) {
            Scalar time = UnitSystem.SI().apply(Quantity.of(event.utime - tic, NonSI.MICRO_SECOND));
            offlinePoseEstimator.event(time, event.channel, ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN));
            // System.out.println("bin blob");
          }
        }
        if (GokartLcmChannel.POSE_LIDAR.equals(event.channel)) {
          // TODO declare outside and encodeBuffer.reset()
          LCMDataOutputStream encodeBuffer = new LCMDataOutputStream(new byte[1024]);
          GokartPoseEvent gokartPoseEvent = offlinePoseEstimator.getGokartPoseEvent();
          BinaryBlob binaryBlob = BinaryBlobs.create(gokartPoseEvent.asArray());
          binaryBlob.encode(encodeBuffer);
          event.data = Arrays.copyOf(encodeBuffer.getBuffer(), encodeBuffer.size());
        }
        logWriter.write(event);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    }
    logWriter.close();
    // ---
    OfflineLogPlayer.process(dst, MessageConsistency.INSTANCE);
  }
}
