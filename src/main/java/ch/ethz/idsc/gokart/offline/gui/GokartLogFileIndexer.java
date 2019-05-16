// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.u3.GokartLabjackFrame;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

// TODO JPH the list here, in the image and the display in the cutter are redundant
public class GokartLogFileIndexer implements OfflineLogListener {
  public static GokartLogFileIndexer create(File file) throws IOException {
    GokartLogFileIndexer gokartLogFileIndexer = new GokartLogFileIndexer(file);
    gokartLogFileIndexer.addGokartPoseListener(new PoseQualityRow());
    gokartLogFileIndexer.addSteerGetListener(new SteerActiveRow());
    gokartLogFileIndexer.addSteerGetListener(new SteerRefTorRow());
    gokartLogFileIndexer.addGokartStatusListener(new SteerAngleRow());
    // ---
    gokartLogFileIndexer.append(0);
    Scalar mb = RationalScalar.of(file.length(), 1000_000_000);
    System.out.print("building index... " + mb.map(Round._2) + " GB ");
    OfflineLogPlayer.process(file, gokartLogFileIndexer);
    System.out.println("done.");
    return gokartLogFileIndexer;
  }

  // ---
  private static final String CHANNEL_DAVIS_IMU = //
      DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final Scalar RESOLUTION = Quantity.of(0.25, SI.SECOND);
  // ---
  private final File file;
  private final List<Integer> raster2event = new ArrayList<>();
  private final TableBuilder raster2autoButton = new TableBuilder();
  private final TableBuilder raster2speed = new TableBuilder();
  private final TableBuilder raster2gyroZ = new TableBuilder();
  final List<GokartLogImageRow> gokartLogImageRows = new LinkedList<>();
  private final List<SteerGetListener> steerGetListeners = new LinkedList<>();
  private final List<GokartStatusListener> gokartStatusListeners = new LinkedList<>();
  private final List<GokartPoseListener> gokartPoseListeners = new LinkedList<>();
  // ---
  private int event_count;
  private Scalar auton = RealScalar.ZERO;
  private Scalar gyroz = RealScalar.ZERO;
  private Tensor rates = Array.zeros(2);

  private GokartLogFileIndexer(File file) {
    this.file = file;
  }

  private void addSteerGetListener(SteerGetListener steerGetListener) {
    gokartLogImageRows.add((GokartLogImageRow) steerGetListener);
    steerGetListeners.add(steerGetListener);
  }

  private void addGokartStatusListener(GokartStatusListener gokartStatusListener) {
    gokartLogImageRows.add((GokartLogImageRow) gokartStatusListener);
    gokartStatusListeners.add(gokartStatusListener);
  }

  private void addGokartPoseListener(GokartPoseListener gokartPoseListener) {
    gokartLogImageRows.add((GokartLogImageRow) gokartPoseListener);
    gokartPoseListeners.add(gokartPoseListener);
  }

  private void append(int count) {
    raster2event.add(count);
    gokartLogImageRows.forEach(GokartLogImageRow::append);
    raster2autoButton.appendRow(auton);
    raster2gyroZ.appendRow(gyroz);
    raster2speed.appendRow(rates);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    int index = time.divide(RESOLUTION).number().intValue();
    if (raster2event.size() <= index)
      append(event_count);
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
      rates = rimoGetEvent.getAngularRate_Y_pair().map(Magnitude.PER_SECOND).map(Scalar::abs);
      // Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      // Scalar raw = Magnitude.VELOCITY.apply(speed.abs()); // abs !
      // raster2speed.set(index, Max.of(raw, raster2speed.get(index)));
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      gokartPoseListeners.forEach(gokartPoseListener -> gokartPoseListener.getEvent(gokartPoseEvent));
    } else //
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      steerGetListeners.forEach(steerGetListener -> steerGetListener.getEvent(steerGetEvent));
    } else //
    // if (channel.equals(GokartLcmChannel.JOYSTICK)) {
    // JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
    // ManualControlInterface manualControlInterface = (ManualControlInterface) joystickEvent;
    // auton = Boole.of(manualControlInterface.isAutonomousPressed());
    // } else //
    if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC)) {
      GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(byteBuffer);
      auton = Boole.of(gokartLabjackFrame.isAutonomousPressed());
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(byteBuffer);
      gokartStatusListeners.forEach(gokartStatusListener -> gokartStatusListener.getEvent(gokartStatusEvent));
    } else //
    if (channel.equals(CHANNEL_DAVIS_IMU)) {
      DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
      gyroz = Magnitude.PER_SECOND.apply(SensorsConfig.GLOBAL.davisGyroZ(davisImuFrame));
    }
    ++event_count;
  }

  public File file() {
    return file;
  }

  public Stream<Tensor> raster2autoButton() {
    return raster2autoButton.stream();
  }

  public Stream<Tensor> raster2gyroZ() {
    return raster2gyroZ.stream();
  }

  public Stream<Tensor> raster2speed() {
    return raster2speed.stream();
  }

  public int getEventIndex(int x0) {
    return raster2event.get(x0);
  }

  public int getRasterSize() {
    return raster2event.size();
  }
}
