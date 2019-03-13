package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.*;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Tan;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

// TODO take into account tilt of lidar
@Deprecated // only used for try out
public class Vlp16Render extends LidarRender {
    private static final int LASERS = 16;
    private final Scalar vlp16Height; // = SensorsConfig.vlp16Height
    private final Tensor angles;
    private final Tensor distances;

    public Vlp16Render(Scalar vlp16Height, GokartPoseInterface gokartPoseInterface) {
        super(gokartPoseInterface);
        this.vlp16Height = Magnitude.METER.apply(vlp16Height);
        angles = Tensors.vector(i -> RealScalar.of(degree(i)), LASERS);
        distances = Tensor.of(angles.stream().filter(s -> Scalars.lessThan(RealScalar.ZERO, s.Get())) //
                .map(s -> distance(s.Get().divide(RealScalar.of(180)).multiply(RealScalar.of(Math.PI)))));
        System.out.println(distances);
        System.out.println(angles);
    }

    // from retina.lidar.vlp16.StaticHelper
    private int degree(int laserId) {
        if (laserId < 0)
            throw new RuntimeException();
        if (laserId == 15)
            return 15;
        return -15 + laserId * 16 % 30;
    }

    private Scalar distance(Scalar angle) {
        return Quantity.of(vlp16Height.divide(Tan.of(angle)), SI.METER);
    }

    public Shape[] circles(GeometricLayer geometricLayer) {
        Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
        double posx = point2D.getX();
        double posy = point2D.getY();
        return distances.stream().map(s -> {
            Point2D width = geometricLayer.toPoint2D(Tensors.vector(s.Get().number().doubleValue(), 0));
            double distance = point2D.distance(width);
            return new Ellipse2D.Double(posx - distance, posy - distance, 2 * distance, 2 * distance);
        }).toArray(Shape[]::new);
    }

    @Override //  from LidarRenderer
    public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
        graphics.setColor(Color.ORANGE);
        Arrays.stream(circles(geometricLayer)).forEach(graphics::draw);
        geometricLayer.popMatrix();
    }
}
