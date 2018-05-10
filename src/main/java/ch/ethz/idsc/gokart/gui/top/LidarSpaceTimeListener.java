package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialListener;

public class LidarSpaceTimeListener implements LidarSpacialListener {
  @Override
  public void lidarSpacial(LidarSpacialEvent lidarSpacialEvent) {
    // System.out.println("time"+lidarSpacialEvent.usec);
    float[] coords = lidarSpacialEvent.coords;
    int usec = lidarSpacialEvent.usec;
  }
}
