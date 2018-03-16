// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class SideObstacleLidarRender extends LidarRender {
  public SideObstacleLidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      Tensor translate = Se2Utils.toSE2Matrix(Tensors.vector( //
          0, // translation right (in pixel space)
          0, // translation up (in pixel space) TODO VC use SensorsConfig.GLOBAL.vlp16Height to
          0 // rotation is pixel space
      ));
      geometricLayer.pushMatrix(translate);
      // ---
      graphics.setColor(color);
      SimpleSpacialObstaclePredicate simpleSpacialObstaclePredicate= new SimpleSpacialObstaclePredicate(SafetyConfig.GLOBAL.vlp16_ZLo, // take from SafetyConfig.GLOBAL.
				SafetyConfig.GLOBAL.vlp16_ZHi, // take from SafetyConfig.GLOBAL.
				SensorsConfig.GLOBAL.vlp16_incline);
      // TODO VC create an instance of SimpleSpacialObstaclePredicate
      for (Tensor x : points) {
    	  if (simpleSpacialObstaclePredicate.isObstacle(x)) {
    		  Tensor v = Tensors.of(x.Get(0), x.Get(2));
    		  Point2D point2D = geometricLayer.toPoint2D(v);
    	      graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
    	  }
     
        // TODO VC use instance of SimpleSpacialObstaclePredicate to get status about x
//        double z = x.Get(2).number().doubleValue() + x.Get(0).number().doubleValue() * SensorsConfig.GLOBAL.vlp16_incline.number().doubleValue();
//        // add offset correction based on SensorsConfig.GLOBAL.vlp16_incline
//        if (z > Magnitude.METER.apply(SafetyConfig.GLOBAL.vlp16_ZLo).number().doubleValue()
//            && z < Magnitude.METER.apply(SafetyConfig.GLOBAL.vlp16_ZHi).number().doubleValue()) {
//          Tensor v = Tensors.of(x.Get(0), x.Get(2));
//          Point2D point2D = geometricLayer.toPoint2D(v);
//          // System.out.println(point2D);
//          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
//        }
      }
      // ---
      geometricLayer.popMatrix();
    }
    geometricLayer.popMatrix();
  }
}
