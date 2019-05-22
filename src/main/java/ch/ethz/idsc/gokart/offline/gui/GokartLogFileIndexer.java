// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionStepsMessage;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.u3.GokartLabjackFrame;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.joystick.ManualControlListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

// TODO JPH the list here, in the image and the display in the cutter are redundant
public class GokartLogFileIndexer implements OfflineLogListener {
  public static GokartLogFileIndexer create(File file) throws IOException {
    GokartLogFileIndexer gokartLogFileIndexer = new GokartLogFileIndexer(file);
    gokartLogFileIndexer.addRow(new AutonomousButtonRow());
    gokartLogFileIndexer.addRow(new PoseQualityRow());
    gokartLogFileIndexer.addRow(new SteerActiveRow());
    gokartLogFileIndexer.addRow(new SteerRefTorRow());
    gokartLogFileIndexer.addRow(new SteerAngleRow());
    gokartLogFileIndexer.addRow(new RimoRateRow(0));
    gokartLogFileIndexer.addRow(new RimoRateRow(1));
    gokartLogFileIndexer.addRow(new LinmotPositionRow());
    gokartLogFileIndexer.addRow(new LinmotOperationalRow());
    gokartLogFileIndexer.addRow(new ResetButtonRow());
    gokartLogFileIndexer.addRow(new Vmu931AccRow(0));
    gokartLogFileIndexer.addRow(new Vmu931AccRow(1));
    gokartLogFileIndexer.addRow(new MpcCountRow());
    // ---
    gokartLogFileIndexer.append(0);
    Scalar mb = RationalScalar.of(file.length(), 1000_000_000);
    System.out.print("building index... " + mb.map(Round._2) + " GB ");
    OfflineLogPlayer.process(file, gokartLogFileIndexer);
    System.out.println("done.");
    return gokartLogFileIndexer;
  }

  // ---
  private static final Scalar RESOLUTION = Quantity.of(0.25, SI.SECOND);
  // ---
  private final File file;
  private final List<Integer> raster2event = new ArrayList<>();
  // ---
  final List<GokartLogImageRow> gokartLogImageRows = new LinkedList<>();
  private final List<SteerGetListener> steerGetListeners = new LinkedList<>();
  private final List<LinmotGetListener> linmotGetListeners = new LinkedList<>();
  private final List<GokartStatusListener> gokartStatusListeners = new LinkedList<>();
  private final List<ManualControlListener> manualControlListeners = new LinkedList<>();
  private final List<GokartPoseListener> gokartPoseListeners = new LinkedList<>();
  private final List<RimoGetListener> rimoGetListeners = new LinkedList<>();
  private final List<Vmu931ImuFrameListener> vmu931ImuFrameListeners = new LinkedList<>();
  private final List<MPCControlUpdateListener> mpcControlUpdateListeners = new LinkedList<>();
  // ---
  private int event_count;

  private GokartLogFileIndexer(File file) {
    this.file = file;
  }

  private void addRow(GokartLogImageRow gokartLogImageRow) {
    gokartLogImageRows.add(gokartLogImageRow);
    if (gokartLogImageRow instanceof SteerGetListener)
      steerGetListeners.add((SteerGetListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof LinmotGetListener)
      linmotGetListeners.add((LinmotGetListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof RimoGetListener)
      rimoGetListeners.add((RimoGetListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof GokartStatusListener)
      gokartStatusListeners.add((GokartStatusListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof ManualControlListener)
      manualControlListeners.add((ManualControlListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof GokartPoseListener)
      gokartPoseListeners.add((GokartPoseListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof Vmu931ImuFrameListener)
      vmu931ImuFrameListeners.add((Vmu931ImuFrameListener) gokartLogImageRow);
    else //
    if (gokartLogImageRow instanceof MPCControlUpdateListener)
      mpcControlUpdateListeners.add((MPCControlUpdateListener) gokartLogImageRow);
  }

  private void append(int count) {
    raster2event.add(count);
    gokartLogImageRows.forEach(GokartLogImageRow::append);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    int index = time.divide(RESOLUTION).number().intValue();
    if (raster2event.size() <= index)
      append(event_count);
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      steerGetListeners.forEach(listener -> listener.getEvent(steerGetEvent));
    } else //
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      linmotGetListeners.forEach(listener -> listener.getEvent(linmotGetEvent));
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
      rimoGetListeners.forEach(listener -> listener.getEvent(rimoGetEvent));
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      gokartPoseListeners.forEach(listener -> listener.getEvent(gokartPoseEvent));
    } else //
    if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC)) {
      GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(byteBuffer);
      manualControlListeners.forEach(listener -> listener.manualControl(gokartLabjackFrame));
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(byteBuffer);
      gokartStatusListeners.forEach(listener -> listener.getEvent(gokartStatusEvent));
    } else //
    if (channel.equals(GokartLcmChannel.VMU931_AG)) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      vmu931ImuFrameListeners.forEach(listener -> listener.vmu931ImuFrame(vmu931ImuFrame));
    } else //
    if (channel.equals(GokartLcmChannel.MPC_FORCES_CNS)) {
      ControlAndPredictionSteps controlAndPredictionSteps = //
          new ControlAndPredictionStepsMessage(byteBuffer).getPayload();
      mpcControlUpdateListeners.forEach(listener -> listener.getControlAndPredictionSteps(controlAndPredictionSteps));
    }
    ++event_count;
  }

  public File file() {
    return file;
  }

  public int getEventIndex(int x0) {
    return raster2event.get(x0);
  }

  public int getRasterSize() {
    return raster2event.size();
  }
}
