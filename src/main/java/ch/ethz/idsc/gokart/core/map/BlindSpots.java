package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.*;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

public class BlindSpots {
    private Tensor angles = Tensors.empty();

    public static BlindSpots defaultGokart() {
        BlindSpots blindSpots = new BlindSpots();
        blindSpots.add(Tensors.vector(3., 3.4));
        blindSpots.add(Tensors.vector(6.1, 0.2));
        return blindSpots;
    }

    /** @param vector blind spot in azimuths [rad] */
    public void add(Tensor vector) {
        angles.append(VectorQ.requireLength(vector, 2).map(Mod.function(Pi.TWO)::of));
    }

    /** @param azimuth [rad]
     * @return whether the azimuth is in a blind spot */
    public boolean isBlind(Scalar azimuth) {
        return angles.stream().anyMatch(sector -> {
            Scalar start = sector.Get(0);
            Scalar end = sector.Get(1);
            if (Scalars.lessEquals(start, end)) {
                return Scalars.lessEquals(start, azimuth) && Scalars.lessEquals(azimuth, end);
            } else {
                return (Scalars.lessEquals(RealScalar.ZERO, azimuth) && Scalars.lessEquals(azimuth, end)) || //
                        (Scalars.lessEquals(start, azimuth) && Scalars.lessEquals(azimuth, Pi.TWO));
            }
        });
    }
}
