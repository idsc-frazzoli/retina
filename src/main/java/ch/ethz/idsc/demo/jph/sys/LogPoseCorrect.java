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

enum LogPoseCorrect {
  ;
  public static void main(String[] args) throws Exception {
    File src = DatahakiLogFileLocator.file(GokartLogFile._20180412T163109_7e5b46c2);
    File dst = null;
    dst = UserHome.file("20180412T163109_with_pose.lcm");
    if (dst.exists()) {
      System.out.println("deleting: " + dst);
      dst.delete();
    }
    int lo = 184738;
    int hi = 1076858;
    // ---
    GokartPoseEvent gpe = GokartPoseEvents.getPoseEvent(//
        Tensors.fromString("{46.965741254102845[m], 48.42802931327099[m], 1.1587704741034797}"), //
        RealScalar.ONE);
    BinaryBlob bbb = BinaryBlobs.create(gpe.asArray());
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      // int count = 0;
      while (true) {
        Event event = log.readNext();
        if (lo <= event.eventNumber && event.eventNumber < hi) {
          try {
            if (GokartLcmChannel.POSE_LIDAR.equals(event.channel)) {
              BinaryBlob binaryBlob = bbb; // new BinaryBlob(event.data);
              // GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(byteBuffer);
              LCMDataOutputStream encodeBuffer = new LCMDataOutputStream(new byte[1024]);
              binaryBlob.encode(encodeBuffer);
              // byte[] buffer = encodeBuffer.getBuffer();
              event.data = // new byte[encodeBuffer.size()];
                  Arrays.copyOf(encodeBuffer.getBuffer(), encodeBuffer.size());
              // , 0, encodeBuffer.size());
              // event.data = gpe.asArray();
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
