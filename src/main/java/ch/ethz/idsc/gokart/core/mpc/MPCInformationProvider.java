// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** singleton instance */
public class MPCInformationProvider extends MPCControlUpdateListener {
  private final static MPCInformationProvider INSTANCE = new MPCInformationProvider();

  public static MPCInformationProvider getInstance() {
    return INSTANCE;
  }

  private MPCInformationProvider() {
    // ---
  }

  /** get the predicted positions
   * @return predicted X- and Y-position in tensor */
  public Tensor getPositions() {
    // avoid race conditions
    if (Objects.nonNull(cns)) {
      ControlAndPredictionSteps localCNS = cns;
      // TODO use stream notation
      Tensor positions = Tensors.empty();
      for (int i = 0; i < localCNS.steps.length; ++i)
        // TODO make member function in GokartState
        positions.append(//
            Tensors.of( //
                localCNS.steps[i].state.getX(), //
                localCNS.steps[i].state.getY()));
      return positions;
    }
    return Tensors.empty();
  }

  /** get the acceleration at prediction steps */
  public Tensor getAccelerations() {
    if (Objects.nonNull(cns)) {
      ControlAndPredictionSteps localCNS = cns;
      Tensor accelerations = Tensors.empty();
      for (int i = 0; i < localCNS.steps.length; i++)
        accelerations.append(localCNS.steps[i].control.getaB());
      return accelerations;
    }
    return Tensors.empty();
  }

  /** get the poses at steps in {x,y,a} */
  public Tensor getXYA() {
    if (Objects.nonNull(cns)) {
      ControlAndPredictionSteps localCNS = cns;
      Tensor orientations = Tensors.empty();
      for (int i = 0; i < localCNS.steps.length; i++) {
        // TODO make member function in GokartState
        Scalar X = RealScalar.of(localCNS.steps[i].state.getX().number().doubleValue());
        Scalar Y = RealScalar.of(localCNS.steps[i].state.getY().number().doubleValue());
        orientations.append(//
            Tensors.of(//
                X, //
                Y, //
                localCNS.steps[i].state.getPsi()//
            ));
      }
      return orientations;
    }
    return Tensors.empty();
  }
}
