// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.util.gui.RowPanel;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public abstract class InterfaceComponent {
  public static final int MAX_USHORT = 65535;
  // ---
  public static final int HEIGHT = 30;
  // ---
  private final JPanel jPanel = new JPanel(new BorderLayout());
  private final RowPanel rowTitle = new RowPanel();
  private final RowPanel rowActor = new RowPanel();

  public InterfaceComponent() {
    jPanel.add(rowTitle.jPanel, BorderLayout.WEST);
    jPanel.add(rowActor.jPanel, BorderLayout.CENTER);
    { // info: ip port
      JToolBar jToolBar = createRow("IP:PORT");
      jToolBar.add(new JLabel("localhost:1234"));
    }
    { // start/stop connection
      JToolBar jToolBar = createRow("connect");
      SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setList(Arrays.asList(10, 20, 50, 100));
      spinnerLabel.setValue(10);
      spinnerLabel.addToComponentReduced(jToolBar, new Dimension(60, 26), "frequency");
      JToggleButton jToggleButton = new JToggleButton("connect");
      jToolBar.add(jToggleButton);
    }
  }

  public JToolBar createRow(String title) {
    JToolBar jToolBar1 = new JToolBar();
    JToolBar jToolBar2 = new JToolBar();
    jToolBar1.setFloatable(false);
    jToolBar1.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    jToolBar1.add(new JLabel(title));
    jToolBar2.setFloatable(false);
    jToolBar2.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    addPair(jToolBar1, jToolBar2);
    return jToolBar2;
  }

  public void addPair(JComponent west, JComponent center) {
    int width;
    width = west.getPreferredSize().width;
    west.setPreferredSize(new Dimension(width, HEIGHT));
    rowTitle.add(west);
    // ---
    width = center.getPreferredSize().width;
    center.setPreferredSize(new Dimension(width, HEIGHT));
    rowActor.add(center);
  }

  public static JTextField createReading() {
    JTextField jTextField = new JTextField(20);
    jTextField.setText("<unknown>");
    jTextField.setEditable(false);
    return jTextField;
  }

  public Component getComponent() {
    return jPanel;
  }
}
