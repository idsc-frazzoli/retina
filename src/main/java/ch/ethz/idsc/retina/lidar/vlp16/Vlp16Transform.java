// code by gjoel
package ch.ethz.idsc.retina.lidar.vlp16;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Sin;

/** transform lidar coordinates from polar to cartesian, and vice versa
 * not compensating twist and incline */
public enum Vlp16Transform {
    PolarToCartesian {
        /** @param azimuth in [rad]
         * @param elevation in [rad]
         * @param radius in [m]
         * @return Tensor x, y, z in [m] */
        @Override
        public Tensor of(Scalar azimuth, Scalar elevation, Scalar radius) {
            azimuth = azimuth.negate().add(offset);
            /* according to manual
            return Tensors.of( //
                    radius.multiply(Cos.of(elevation)).multiply(Sin.of(azimuth)), //
                    radius.multiply(Cos.of(elevation)).multiply(Cos.of(azimuth)), //
                    radius.multiply(Sin.of(elevation)));
            */
            return Tensors.of( //
                    radius.multiply(Cos.of(elevation)).multiply(Cos.of(azimuth)), //
                    radius.multiply(Cos.of(elevation)).multiply(Sin.of(azimuth)), //
                    radius.multiply(Sin.of(elevation)));
        }
    },

    CartesianToPolar {
        /** @param x in [m]
         * @param y in [m]
         * @param z in [m]
         * @return Tensor azimuth, elevation, radius in [rad], resp. [m] */
        @Override
        public Tensor of(Scalar x, Scalar y, Scalar z) {
            /* according to manual
            return Tensors.of( //
                    ArcTan.of(y, x), //
                    ArcTan.of(Norm._2.of(Tensors.of(x, y)), z), //
                    Norm._2.of(Tensors.of(x, y, z)));
            */
            return Tensors.of( //
                    Mod.function(Pi.TWO).of(offset.subtract(ArcTan.of(x, y))), //
                    ArcTan.of(Norm._2.of(Tensors.of(x, y)), z), //
                    Norm._2.of(Tensors.of(x, y, z)));
        }
    };

    private static Scalar offset = SensorsConfig.GLOBAL.vlp16_twist;

    public Tensor of(Tensor vector) {
        return of(vector.Get(0), vector.Get(1), vector.Get(2));
    }

    public abstract Tensor of(Scalar s1, Scalar s2, Scalar s3);
}
