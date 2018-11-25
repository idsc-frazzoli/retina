package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class MPCInformationProvider extends MPCControlUpdateListener {
  private static MPCInformationProvider INSTANCE;

  public static MPCInformationProvider getInstance() {
    if (INSTANCE == null)
      INSTANCE = new MPCInformationProvider();
    return INSTANCE;
  }

  /** get the predicted positions
   * @return predicted X- and Y-position in tensor */
  public Tensor getPositions() {
    // avoid race conditions
    if (cns != null) {
      ControlAndPredictionSteps localCNS = cns;
      Tensor positions = Tensors.empty();
      for (int i = 0; i < localCNS.steps.length; i++)
        positions.append(//
            Tensors.of(//
                localCNS.steps[i].state.getX(), //
                localCNS.steps[i].state.getY()));
      return positions;
    }
    return Tensors.empty();
  }

  /** get the acceleration at prediction steps */
  public Tensor getAccelerations() {
    if (cns != null) {
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
    if (cns != null) {
      ControlAndPredictionSteps localCNS = cns;
      Tensor orientations = Tensors.empty();
      for (int i = 0; i < localCNS.steps.length; i++) {
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
