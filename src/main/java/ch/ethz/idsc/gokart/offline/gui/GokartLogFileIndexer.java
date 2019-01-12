// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuFramePublisher;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.joystick.JoystickDecoder;
import ch.ethz.idsc.retina.joystick.JoystickEvent;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GokartLogFileIndexer implements OfflineLogListener {
  public static GokartLogFileIndexer create(File file) throws IOException {
    GokartLogFileIndexer lcmLogFileIndexer = new GokartLogFileIndexer(file);
    System.out.print("building index... ");
    OfflineLogPlayer.process(file, lcmLogFileIndexer);
    System.out.println("done.");
    return lcmLogFileIndexer;
  }

  // ---
  private static final String CHANNEL_DAVIS_IMU = //
      DavisImuFramePublisher.channel(GokartLcmChannel.DAVIS_OVERVIEW);
  private static final Scalar RESOLUTION = Quantity.of(0.25, SI.SECOND);
  // ---
  private final File file;
  private final List<Integer> raster2event = new ArrayList<>();
  private final TableBuilder raster2auton = new TableBuilder();
  private final TableBuilder raster2poseq = new TableBuilder();
  private final TableBuilder raster2steer = new TableBuilder();
  private final TableBuilder raster2speed = new TableBuilder();
  private final TableBuilder raster2gyroz = new TableBuilder();
  // ---
  private int event_count;
  private Scalar auton = RealScalar.ZERO;
  private Scalar poseq = RealScalar.ZERO;
  private Scalar steer = RealScalar.ZERO;
  private Scalar gyroz = RealScalar.ZERO;
  private Tensor rates = Array.zeros(2);

  private GokartLogFileIndexer(File file) {
    this.file = file;
    append(0);
  }

  private void append(int count) {
    raster2event.add(count);
    raster2auton.appendRow(auton);
    raster2poseq.appendRow(poseq);
    raster2steer.appendRow(steer);
    raster2gyroz.appendRow(gyroz);
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
      GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(byteBuffer);
      poseq = gokartPoseEvent.getQuality();
    } else //
    if (channel.equals(GokartLcmChannel.JOYSTICK)) {
      JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
      ManualControlInterface manualControlInterface = (ManualControlInterface) joystickEvent;
      auton = Boole.of(manualControlInterface.isAutonomousPressed());
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      SteerColumnInterface steerColumnInterface = new GokartStatusEvent(byteBuffer);
      steer = steerColumnInterface.isSteerColumnCalibrated() //
          ? SteerPutEvent.ENCODER.apply(steerColumnInterface.getSteerColumnEncoderCentered())
          : RealScalar.ZERO;
    } else //
    if (channel.equals(CHANNEL_DAVIS_IMU)) {
      DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
      gyroz = Magnitude.PER_SECOND.apply(SensorsConfig.GLOBAL.getGyroZ(davisImuFrame));
    }
    ++event_count;
  }

  public File file() {
    return file;
  }

  public Stream<Tensor> raster2auton() {
    return raster2auton.stream();
  }

  public Stream<Tensor> raster2poseq() {
    return raster2poseq.stream();
  }

  public Stream<Tensor> raster2steer() {
    return raster2steer.stream();
  }

  public Stream<Tensor> raster2gyroz() {
    return raster2gyroz.stream();
  }

  public Stream<Tensor> raster2speed() {
    return raster2speed.stream();
  }

  public int getEventIndex(int x0) {
    return raster2event.get(x0);
  }
}
