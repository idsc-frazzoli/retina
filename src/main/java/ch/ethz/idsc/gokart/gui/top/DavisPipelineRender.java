// code by mg
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.demo.mg.pipeline.InputSubModule;
import ch.ethz.idsc.demo.mg.pipeline.PhysicalBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class DavisPipelineRender extends AbstractGokartRender implements ActionListener {
  public final InputSubModule inputSubModule = new InputSubModule(new PipelineConfig());
  // ..
  final JToggleButton jToggleButton = new JToggleButton("pipeline");
  private boolean isSelected = false;
  private final double mapAheadDistance = 7; // [m]

  public DavisPipelineRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    jToggleButton.setSelected(isSelected);
    jToggleButton.addActionListener(this);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isSelected)
      return;
    // visualize detected features
    List<PhysicalBlob> features = inputSubModule.getProcessedblobs();
    features.forEach(blob -> drawBlob(geometricLayer, graphics, blob));
  }

  private void drawBlob(GeometricLayer geometricLayer, Graphics2D graphics, PhysicalBlob blob) {
    Tensor mappedFeature = Tensors.vectorDouble(blob.getPos());
    if (mappedFeature.Get(0).number().doubleValue() < mapAheadDistance) {
      // TODO store location of all features
      Point2D point2D = geometricLayer.toPoint2D(mappedFeature);
      graphics.setColor(Color.BLACK);
      graphics.drawOval((int) point2D.getX(), (int) point2D.getY(), 10, 10);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    isSelected = jToggleButton.isSelected();
  }
}
