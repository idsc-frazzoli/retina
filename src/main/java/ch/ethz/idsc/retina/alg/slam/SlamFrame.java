// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

public class SlamFrame {
  // ---
  private final OccupancyMap occupancyMap;
  private final JFrame jFrame = new JFrame();
  public final SlamComponent slamComponent = new SlamComponent();

  public SlamFrame(OccupancyMap occupancyMap) {
    this.occupancyMap = occupancyMap;
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 50, 1100, 1050);
    {
      JPanel jPanel = new JPanel(new BorderLayout());
      {
        JToolBar jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
        {
          SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
          spinnerLabel.setStream(Subdivide.of(10, 200, 19).stream().map(Scalar.class::cast));
          spinnerLabel.setValueSafe(occupancyMap.threshold);
          spinnerLabel.addSpinnerListener(scalar -> occupancyMap.threshold = N.DOUBLE.of(scalar));
          spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "ds");
        }
        {
          SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
          spinnerLabel.setStream(Subdivide.of(0.01, 0.1, 9).map(Round._2).stream().map(Scalar.class::cast));
          spinnerLabel.setValueSafe(occupancyMap.ds_value);
          spinnerLabel.addSpinnerListener(scalar -> occupancyMap.ds_value = N.DOUBLE.of(scalar));
          spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "ds");
        }
        jPanel.add(jToolBar, BorderLayout.NORTH);
      }
      jPanel.add(slamComponent.jComponent, BorderLayout.CENTER);
      jFrame.setContentPane(jPanel);
    }
    jFrame.setVisible(true);
  }
}
