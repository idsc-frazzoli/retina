// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;

public class ViewLcmFrame extends TimerFrame {
  public final JButton jButtonMapCreate = new JButton("map create");
  public final JButton jButtonMapUpdate = new JButton("map update");
  public final JButton jButtonSnap = new JButton("snap to map");
  public final JButton jButtonSetLocation = new JButton("set location");
  private static final Tensor MODEL2PIXEL_INITIAL = Tensors.matrix(new Number[][] { //
      { 7.5, 0, 0 }, //
      { 0, -7.5, 640 }, //
      { 0, 0, 1 }, //
  }).unmodifiable();
  private GokartPoseInterface gokartPoseInterface;

  public ViewLcmFrame() {
    jToolBar.add(jButtonMapCreate);
    jToolBar.add(jButtonMapUpdate);
    jToolBar.add(jButtonSnap);
    jButtonSetLocation.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Tensor model2pixel = geometricComponent.getModel2Pixel();
        Tensor state = gokartPoseInterface.getPose(); // {x[m],y[y],angle[]}
        state = state.map(s -> RealScalar.of(s.number()));
        Tensor pose = Se2Utils.toSE2Matrix(state);
        Tensor newPose = Inverse.of(MODEL2PIXEL_INITIAL).dot(model2pixel).dot(pose); // TODO use linear solve
        Tensor newState = Se2Utils.fromSE2Matrix(newPose);
        newState.set(s -> Quantity.of(s.Get(), "m"), 0);
        newState.set(s -> Quantity.of(s.Get(), "m"), 1);
        gokartPoseInterface.setPose(newState);
        geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
      }
    });
    jToolBar.add(jButtonSetLocation);
  }

  protected void setGokartPoseInterface(GokartPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
  }
}
