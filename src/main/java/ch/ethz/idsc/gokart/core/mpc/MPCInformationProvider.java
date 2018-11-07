package ch.ethz.idsc.gokart.core.mpc;

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
    } else
      return Tensors.empty();
  }
}
