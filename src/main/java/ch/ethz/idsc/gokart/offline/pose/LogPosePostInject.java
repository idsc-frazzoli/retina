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
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.lcm.BinaryBlobs;
import ch.ethz.idsc.gokart.lcm.MessageConsistency;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResult;
import ch.ethz.idsc.gokart.offline.slam.LocalizationResultListener;
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
public class LogPosePostInject implements LocalizationResultListener {
  // private static final String POST_POSE = ;
  // ---
  private LocalizationResult localizationResult = null;

  public void process(File src, File dst, OfflineLogListener offlineLogListener) throws Exception {
    if (dst.exists())
      throw new RuntimeException();
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logEventWriter = new LogEventWriter(dst);
    Set<String> set = new HashSet<>();
    Long tic = null;
    System.out.println("start");
    try {
      while (true) {
        // TODO code redundant to OfflineLogPlayer
        Event event = log.readNext();
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
            offlineLogListener.event(time, event.channel, ByteBuffer.wrap(binaryBlob.data).order(ByteOrder.LITTLE_ENDIAN));
            // ---
            if (Objects.nonNull(localizationResult)) {
              // System.out.println("inject");
              GokartPoseEvent gokartPoseEvent = GokartPoseEvents.create( //
                  GokartPoseHelper.attachUnits(localizationResult.pose_xyt), //
                  localizationResult.ratio);
              // ---
              LCMDataOutputStream encodeBuffer = new LCMDataOutputStream(new byte[1024]);
              BinaryBlob post_binaryBlob = BinaryBlobs.create(gokartPoseEvent.asArray());
              post_binaryBlob.encode(encodeBuffer);
              Event post_event = new Event();
              post_event.utime = event.utime;
              post_event.data = Arrays.copyOf(encodeBuffer.getBuffer(), encodeBuffer.size());
              post_event.channel = GokartPosePostChannel.INSTANCE.channel();
              logEventWriter.write(post_event);
              // ---
              localizationResult = null;
            }
          }
        }
        logEventWriter.write(event);
      }
    } catch (Exception exception) {
      String message = exception.getMessage();
      if (Objects.isNull(message) || !message.equals("EOF"))
        exception.printStackTrace();
    }
    logEventWriter.close();
    // ---
    OfflineLogPlayer.process(dst, MessageConsistency.INSTANCE);
  }

  @Override // from LocalizationResultListener
  public void localizationCallback(LocalizationResult localizationResult) {
    this.localizationResult = localizationResult;
  }
}
