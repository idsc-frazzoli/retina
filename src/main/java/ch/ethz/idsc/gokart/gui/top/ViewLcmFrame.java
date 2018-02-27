// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ViewLcmFrame extends TimerFrame {
  public final JButton jButtonMapCreate = new JButton("map create");
  public final JButton jButtonMapUpdate = new JButton("map update");
  public final JButton jButtonSetLocation1 = new JButton("1 set");
  public final JButton jButtonSnap = new JButton("2 snap");
  public final JButton jButtonSetLocation2 = new JButton("3 set (again)");
  public final JToggleButton jToggleButton = new JToggleButton("4 track");
  public static final Tensor MODEL2PIXEL_INITIAL = Tensors.matrix(new Number[][] { //
      { 7.5, 0, 0 }, //
      { 0, -7.5, 640 }, //
      { 0, 0, 1 }, //
  }).unmodifiable();
  private MappedPoseInterface gokartPoseInterface;
  ActionListener al = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      Tensor model2pixel = geometricComponent.getModel2Pixel();
      Tensor state = gokartPoseInterface.getPose(); // {x[m],y[y],angle[]}
      state = state.map(s -> RealScalar.of(s.number()));
      Tensor pose = Se2Utils.toSE2Matrix(state);
      // Tensor newPose = Inverse.of(MODEL2PIXEL_INITIAL).dot(model2pixel).dot(pose);
      Tensor newPose = LinearSolve.of(MODEL2PIXEL_INITIAL, model2pixel.dot(pose));
      Tensor newState = Se2Utils.fromSE2Matrix(newPose);
      newState.set(s -> Quantity.of(s.Get(), SI.METER), 0);
      newState.set(s -> Quantity.of(s.Get(), SI.METER), 1);
      System.out.println("new state=" + newState);
      gokartPoseInterface.setPose(newState, RealScalar.ONE);
      geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
    }
  };

  public ViewLcmFrame() {
    jToolBar.add(jButtonMapCreate);
    jToolBar.add(jButtonMapUpdate);
    jButtonSetLocation1.addActionListener(al);
    jToolBar.add(jButtonSetLocation1);
    jToolBar.add(jButtonSnap);
    jButtonSetLocation2.addActionListener(al);
    jToolBar.add(jButtonSetLocation2);
    jToolBar.add(jToggleButton);
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
    // Tensors.fromString("{{7.5,0,300},{0,-7.5,300},{0,0,1}}"));
    Tensor tensor = geometricComponent.getModel2Pixel();
    System.out.println("m2p=" + Pretty.of(tensor));
  }

  protected void setGokartPoseInterface(MappedPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
  }
}
