// code by mg
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.blobtrack.algo.BlobTrackProvider;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

// TODO depending on future development, maybe move drawing fct to VisualizationUtil
public class DavisPipelineRender extends AbstractGokartRender implements ActionListener {
  private AccumulatedFeaturePoints accumulatedFeaturePoints;
  public final BlobTrackProvider pipelineProvider = new BlobTrackProvider(new BlobTrackConfig());
  final ColorDataIndexed colorDataIndexed = ColorDataLists._250.cyclic();
  final JToggleButton jToggleButton = new JToggleButton("pipeline");
  private boolean isSelected = false;
  private final double mapAheadDistance = 7; // [m]
  private final int circleSize = 10; // [pixel]

  public DavisPipelineRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    jToggleButton.setSelected(isSelected);
    jToggleButton.addActionListener(this);
    accumulatedFeaturePoints = new AccumulatedFeaturePoints();
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isSelected)
      return;
    // visualize detected features
    List<PhysicalBlob> features = pipelineProvider.getPhysicalBlobs();
    features.forEach(blob -> accumulateBlobs(geometricLayer, graphics, blob));
    for (int i = 0; i < accumulatedFeaturePoints.getAccumulatedPoints().size(); i++) {
      int blobID = accumulatedFeaturePoints.getBlobIDList().get(i);
      graphics.setColor(colorDataIndexed.getColor(blobID));
      Point2D point = accumulatedFeaturePoints.getAccumulatedPoints().get(i);
      graphics.drawOval((int) point.getX() - circleSize / 2, (int) point.getY() - circleSize / 2, circleSize, circleSize);
    }
  }

  private void accumulateBlobs(GeometricLayer geometricLayer, Graphics2D graphics, PhysicalBlob blob) {
    Tensor mappedFeature = Tensors.vectorDouble(blob.getPos());
    if (mappedFeature.Get(0).number().doubleValue() < mapAheadDistance) {
      Point2D point2D = geometricLayer.toPoint2D(mappedFeature);
      accumulatedFeaturePoints.addFeaturePoint(point2D, blob.getBlobID());
    }
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent e) {
    isSelected = jToggleButton.isSelected();
  }
}
