// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosListener;

public class VelodyneRayFrame implements LidarRayBlockListener, VelodynePosListener {
  public final JFrame jFrame = new JFrame();
  private VelodyneRayComponent hdl32eRayComponent = new VelodyneRayComponent();

  public VelodyneRayFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 600, 600);
    jFrame.setContentPane(hdl32eRayComponent.jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    RayContainer rayContainer = new RayContainer();
    rayContainer.position = Arrays.copyOf(lidarRayBlockEvent.floatBuffer.array(), lidarRayBlockEvent.floatBuffer.limit());
    rayContainer.intensity = Arrays.copyOf(lidarRayBlockEvent.byteBuffer.array(), lidarRayBlockEvent.byteBuffer.limit());
    hdl32eRayComponent.rayContainer = rayContainer;
    hdl32eRayComponent.jComponent.repaint();
  }

  @Override
  public void velodynePos(VelodynePosEvent hdl32ePosEvent) {
    hdl32eRayComponent.hdl32ePosEvent = hdl32ePosEvent;
  }
}
