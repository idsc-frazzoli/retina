// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
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
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
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
    gokartLogFileIndexer.addManualControlListener(new AutonomousButtonRow());
    gokartLogFileIndexer.addGokartPoseListener(new PoseQualityRow());
    gokartLogFileIndexer.addSteerGetListener(new SteerActiveRow());
    gokartLogFileIndexer.addSteerGetListener(new SteerRefTorRow());
    gokartLogFileIndexer.addGokartStatusListener(new SteerAngleRow());
    gokartLogFileIndexer.addRimoGetListeners(new RimoRateRow(0));
    gokartLogFileIndexer.addRimoGetListeners(new RimoRateRow(1));
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
  private final List<GokartStatusListener> gokartStatusListeners = new LinkedList<>();
  private final List<ManualControlListener> manualControlListeners = new LinkedList<>();
  private final List<GokartPoseListener> gokartPoseListeners = new LinkedList<>();
  private final List<RimoGetListener> rimoGetListeners = new LinkedList<>();
  // ---
  private int event_count;

  private GokartLogFileIndexer(File file) {
    this.file = file;
  }

  private void addRimoGetListeners(RimoGetListener rimoGetListener) {
    gokartLogImageRows.add((GokartLogImageRow) rimoGetListener);
    rimoGetListeners.add(rimoGetListener);
  }

  private void addSteerGetListener(SteerGetListener steerGetListener) {
    gokartLogImageRows.add((GokartLogImageRow) steerGetListener);
    steerGetListeners.add(steerGetListener);
  }

  private void addGokartStatusListener(GokartStatusListener gokartStatusListener) {
    gokartLogImageRows.add((GokartLogImageRow) gokartStatusListener);
    gokartStatusListeners.add(gokartStatusListener);
  }

  private void addManualControlListener(ManualControlListener manualControlListener) {
    gokartLogImageRows.add((GokartLogImageRow) manualControlListener);
    manualControlListeners.add(manualControlListener);
  }

  private void addGokartPoseListener(GokartPoseListener gokartPoseListener) {
    gokartLogImageRows.add((GokartLogImageRow) gokartPoseListener);
    gokartPoseListeners.add(gokartPoseListener);
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
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
      rimoGetListeners.forEach(rimoGetListener -> rimoGetListener.getEvent(rimoGetEvent));
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      gokartPoseListeners.forEach(gokartPoseListener -> gokartPoseListener.getEvent(gokartPoseEvent));
    } else //
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      steerGetListeners.forEach(steerGetListener -> steerGetListener.getEvent(steerGetEvent));
    } else //
    if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC)) {
      GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(byteBuffer);
      manualControlListeners.forEach(manualControlListener -> manualControlListener.manualControl(gokartLabjackFrame));
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(byteBuffer);
      gokartStatusListeners.forEach(gokartStatusListener -> gokartStatusListener.getEvent(gokartStatusEvent));
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
