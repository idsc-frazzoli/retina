// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.sophus.app.util.SpinnerLabel;

public class LidarPanoramaFrame implements LidarPanoramaListener, AutoCloseable {
  public static final int SCALE_Y = 3;
  // ---
  public final JFrame jFrame = new JFrame();
  final LidarPanoramaTile lpc = new LidarPanoramaTile();

  public LidarPanoramaFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
      jToolBar.setFloatable(false);
      SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setList(Arrays.asList(0, 1, 2, 3, 4, 5));
      spinnerLabel.setValue(SuperGrayscaleLidarPanorama.history);
      spinnerLabel.addSpinnerListener(value -> SuperGrayscaleLidarPanorama.history = value);
      spinnerLabel.addToComponentReduced(jToolBar, new Dimension(120, 28), "history");
      jPanel.add("North", jToolBar);
    }
    JScrollPane jScrollPane = new JScrollPane(lpc.jComponent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    jPanel.add("Center", jScrollPane);
    jFrame.setContentPane(jPanel);
  }

  @Override // from LidarPanoramaListener
  public void lidarPanorama(LidarPanorama lidarPanorama) {
    lpc._lidarPanorama = lidarPanorama;
    lpc.jComponent.setPreferredSize(new Dimension(lidarPanorama.getMaxWidth(), 300));
    lpc.jComponent.repaint();
  }

  @Override // from AutoCloseable
  public void close() {
    System.out.println("autoclose");
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
