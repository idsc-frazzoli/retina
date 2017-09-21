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
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

public class SlamFrame {
  // ---
  private final JFrame jFrame = new JFrame();
  private Scalar threshold = RealScalar.of(30);
  private Scalar ds_value = RealScalar.of(0.03);
  public SlamComponent slamComponent = new SlamComponent();

  public SlamFrame() {
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
          spinnerLabel.setIndex(2);
          spinnerLabel.addSpinnerListener(scalar -> threshold = N.DOUBLE.of(scalar));
          spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "ds");
        }
        {
          SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
          spinnerLabel.setStream(Subdivide.of(0.01, 0.1, 9).map(Round._2).stream().map(Scalar.class::cast));
          spinnerLabel.setIndex(2);
          spinnerLabel.addSpinnerListener(scalar -> ds_value = N.DOUBLE.of(scalar));
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
