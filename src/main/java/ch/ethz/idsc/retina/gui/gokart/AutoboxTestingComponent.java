// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.zhkart.GetListener;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.PutListener;
import ch.ethz.idsc.retina.dev.zhkart.PutProvider;
import ch.ethz.idsc.retina.util.gui.RowPanel;

abstract class AutoboxTestingComponent<GE, PE> implements GetListener<GE>, PutListener<PE>, PutProvider<PE> {
  public static final int MAX_USHORT = 65535;
  // ---
  public static final int WEST_WIDTH = 140;
  public static final int HEIGHT = 30;
  // ---
  private final JPanel jPanel = new JPanel(new BorderLayout());
  private final RowPanel rowTitle = new RowPanel();
  private final RowPanel rowActor = new RowPanel();
  private boolean isJoystickEnabled;

  public AutoboxTestingComponent() {
    jPanel.add(rowTitle.jPanel, BorderLayout.WEST);
    jPanel.add(rowActor.jPanel, BorderLayout.CENTER);
  }

  protected void addSeparator() {
    JLabel jLabelW = new JLabel();
    jLabelW.setBackground(Color.GRAY);
    jLabelW.setOpaque(true);
    JLabel jLabelC = new JLabel();
    jLabelC.setBackground(Color.GRAY);
    jLabelC.setOpaque(true);
    addPair(jLabelW, jLabelC, 5);
  }

  protected JToolBar createRow(String title) {
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
    addPair(west, center, HEIGHT);
  }

  private void addPair(JComponent west, JComponent center, int height) {
    int width;
    // width = west.getPreferredSize().width;
    west.setPreferredSize(new Dimension(WEST_WIDTH, height));
    west.setSize(new Dimension(WEST_WIDTH, height));
    rowTitle.add(west);
    // ---
    width = center.getPreferredSize().width;
    center.setPreferredSize(new Dimension(width, height));
    rowActor.add(center);
  }

  public JTextField createReading(String title) {
    JTextField jTextField = new JTextField(20);
    jTextField.setText("<unknown>");
    jTextField.setEditable(false);
    jTextField.setEnabled(false);
    jTextField.setDisabledTextColor(Color.BLACK);
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

  public void setJoystickEnabled(boolean status) {
    isJoystickEnabled = status;
  }

  protected boolean isJoystickEnabled() {
    return isJoystickEnabled;
  }

  @Override
  public final ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }
}
