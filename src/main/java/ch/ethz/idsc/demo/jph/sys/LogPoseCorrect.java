// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.BinaryBlobs;
import ch.ethz.idsc.retina.lcm.MessageConsistency;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import idsc.BinaryBlob;
import lcm.lcm.LCMDataOutputStream;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

/** demo */
enum LogPoseCorrect {
  ;
  public static void main(String[] args) throws Exception {
    File src = DatahakiLogFileLocator.file(GokartLogFile._20180412T163855_7e5b46c2);
    File dst = null;
    dst = UserHome.file("20180412T163855_pose.lcm");
    if (dst.exists()) {
      System.out.println("deleting: " + dst);
      dst.delete();
    }
    int lo = 316075;
    int hi = 1076858;
    // ---
    GokartPoseEvent gpe = GokartPoseEvents.getPoseEvent( //
        Tensors.fromString("{46.92496702465816[m], 48.60602413267636[m], 1.1602311755823995}"), //
        RealScalar.ONE);
    final BinaryBlob binaryBlob = BinaryBlobs.create(gpe.asArray());
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      while (true) {
        Event event = log.readNext();
        if (lo <= event.eventNumber && event.eventNumber < hi) {
          try {
            if (GokartLcmChannel.POSE_LIDAR.equals(event.channel)) {
              LCMDataOutputStream encodeBuffer = new LCMDataOutputStream(new byte[1024]);
              binaryBlob.encode(encodeBuffer);
              event.data = Arrays.copyOf(encodeBuffer.getBuffer(), encodeBuffer.size());
            }
            logWriter.write(event);
          } catch (Exception exception) {
            // ---
            exception.printStackTrace();
          }
        }
      }
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      // ---
    }
    logWriter.close();
    // ---
    OfflineLogPlayer.process(dst, MessageConsistency.INSTANCE);
  }
}
