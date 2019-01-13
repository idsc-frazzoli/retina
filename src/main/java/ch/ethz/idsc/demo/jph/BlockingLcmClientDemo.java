// code by jph
package ch.ethz.idsc.demo.jph;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

enum BlockingLcmClientDemo implements DavisImuFrameListener, GokartPoseListener, Runnable {
  INSTANCE;
  Thread thread = new Thread(this);

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    System.out.println("imu");
  }

  @Override
  public void getEvent(GokartPoseEvent getEvent) {
    System.out.println("pose");
    ferry = getEvent;
    thread.interrupt();
  }

  GokartPoseEvent ferry;

  @Override
  public void run() {
    while (true) {
      GokartPoseEvent _ferry = ferry;
      if (Objects.nonNull(_ferry)) {
        ferry = null;
        System.out.println("HANDLE");
        RandomVariate.of(NormalDistribution.standard(), 1000, 1000);
      } else {
        System.out.println("ferry == null");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          System.err.println("interrupted");
          // e.printStackTrace();
        }
      }
    }
  }

  public static void main(String[] args) {
    DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
    GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    gokartPoseLcmClient.addListener(INSTANCE);
    davisImuLcmClient.addListener(INSTANCE);
    gokartPoseLcmClient.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
    INSTANCE.thread.start();
    // Thread.sleep(10000);
  }
}
