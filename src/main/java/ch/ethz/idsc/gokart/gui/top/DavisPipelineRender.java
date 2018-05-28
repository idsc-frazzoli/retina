// code by mg
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.demo.mg.pipeline.PipelineProvider;
import ch.ethz.idsc.demo.mg.gui.AccumulatedFeaturePoints;
import ch.ethz.idsc.demo.mg.pipeline.PhysicalBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class DavisPipelineRender extends AbstractGokartRender implements ActionListener {
  private AccumulatedFeaturePoints accumulatedFeaturePoints;
  public final PipelineProvider pipelineProvider = new PipelineProvider(new PipelineConfig());
  // ..
  final JToggleButton jToggleButton = new JToggleButton("pipeline");
  private boolean isSelected = false;
  private final double mapAheadDistance = 7; // [m]

  public DavisPipelineRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    jToggleButton.setSelected(isSelected);
    jToggleButton.addActionListener(this);
    accumulatedFeaturePoints = new AccumulatedFeaturePoints();
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isSelected)
      return;
    // visualize detected features
    List<PhysicalBlob> features = pipelineProvider.getProcessedblobs();
    features.forEach(blob -> drawBlob(geometricLayer, graphics, blob));
    features.forEach(blob -> accumulateBlobs(geometricLayer, graphics, blob));
    for (int i = 0; i < accumulatedFeaturePoints.getAccumulatedPoints().size(); i++) {
      int blobID = accumulatedFeaturePoints.getBlobIDList().get(i);
      graphics.setColor(Color.BLACK);
      Point2D point = accumulatedFeaturePoints.getAccumulatedPoints().get(i);
      graphics.drawOval((int) point.getX(), (int) point.getY(), 10, 10);
    }
  }

  private void drawBlob(GeometricLayer geometricLayer, Graphics2D graphics, PhysicalBlob blob) {
    Tensor mappedFeature = Tensors.vectorDouble(blob.getPos());
    if (mappedFeature.Get(0).number().doubleValue() < mapAheadDistance) {
      Point2D point = geometricLayer.toPoint2D(mappedFeature);
      graphics.setColor(Color.BLACK);
      graphics.drawOval((int) point.getX(), (int) point.getY(), 10, 10);
    }
  }

  private void accumulateBlobs(GeometricLayer geometricLayer, Graphics2D graphics, PhysicalBlob blob) {
    Tensor mappedFeature = Tensors.vectorDouble(blob.getPos());
    if (mappedFeature.Get(0).number().doubleValue() < mapAheadDistance) {
      Point2D point2D = geometricLayer.toPoint2D(mappedFeature);
      accumulatedFeaturePoints.addFeaturePoint(point2D, blob.getblobID());
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    isSelected = jToggleButton.isSelected();
  }
}
