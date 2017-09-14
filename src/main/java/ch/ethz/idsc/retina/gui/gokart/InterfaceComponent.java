// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Timer;

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
  public static final int WEST_WIDTH = 140;
  public static final int HEIGHT = 30;
  // ---
  private final JPanel jPanel = new JPanel(new BorderLayout());
  private final JLabel jConnectionInfo = new JLabel();
  private final SpinnerLabel<Integer> spinnerLabelPeriod = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("connect");
  private final RowPanel rowTitle = new RowPanel();
  private final RowPanel rowActor = new RowPanel();
  public Timer timer = null;
  ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      boolean isSelected = jToggleButton.isSelected();
      spinnerLabelPeriod.setEnabled(!isSelected);
      connectAction(spinnerLabelPeriod.getValue(), isSelected);
    }
  };

  public InterfaceComponent() {
    jPanel.add(rowTitle.jPanel, BorderLayout.WEST);
    jPanel.add(rowActor.jPanel, BorderLayout.CENTER);
    { // info: ip port
      JToolBar jToolBar = createRow("IP:PORT");
      jToolBar.add(jConnectionInfo);
    }
    { // start/stop connection
      JToolBar jToolBar = createRow("connect");
      spinnerLabelPeriod.setList(Arrays.asList(10, 20, 50, 100, 200, 500, 1000));
      spinnerLabelPeriod.setValue(100); // TODO magic const
      spinnerLabelPeriod.addToComponentReduced(jToolBar, new Dimension(60, 26), "period [ms]");
      jToggleButton.addActionListener(actionListener);
      jToolBar.add(jToggleButton);
    }
  }

  public JToolBar createRow(String title) {
    jConnectionInfo.setText(connectionInfo());
    JToolBar jToolBar1 = new JToolBar();
    JToolBar jToolBar2 = new JToolBar();
    jToolBar1.setFloatable(false);
    jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
    JLabel jLabel = new JLabel(title);
    jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, HEIGHT));
    jToolBar1.add(jLabel);
    jToolBar2.setFloatable(false);
    jToolBar2.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    addPair(jToolBar1, jToolBar2);
    return jToolBar2;
  }

  private void addPair(JComponent west, JComponent center) {
    int width;
    // width = west.getPreferredSize().width;
    west.setPreferredSize(new Dimension(WEST_WIDTH, HEIGHT));
    west.setSize(new Dimension(WEST_WIDTH, HEIGHT));
    rowTitle.add(west);
    // ---
    width = center.getPreferredSize().width;
    center.setPreferredSize(new Dimension(width, HEIGHT));
    rowActor.add(center);
  }

  public JTextField createReading(String title) {
    JTextField jTextField = new JTextField(20);
    jTextField.setText("<unknown>");
    jTextField.setEditable(false);
    jConnectionInfo.setText(connectionInfo());
    JToolBar jToolBar1 = new JToolBar();
    jToolBar1.setFloatable(false);
    jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
    JLabel jLabel = new JLabel(title);
    jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, HEIGHT));
    jToolBar1.add(jLabel);
    addPair(jToolBar1, jTextField);
    return jTextField;
  }

  public Component getComponent() {
    return jPanel;
  }

  public abstract void connectAction(int period, boolean isSelected);

  public abstract String connectionInfo();
}
