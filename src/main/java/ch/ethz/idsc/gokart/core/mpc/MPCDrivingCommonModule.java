// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.io.Timing;

/* package */ abstract class MPCDrivingCommonModule extends MPCDrivingAbstractModule {
  MPCDrivingCommonModule(MPCRequestPublisher mpcRequestPublisher, Timing timing) {
    super(mpcRequestPublisher, timing);
  }

  MPCDrivingCommonModule(MPCRequestPublisher mpcRequestPublisher, MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing,
      MPCPreviewableTrack track) {
    super(mpcRequestPublisher, mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  final MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    return new MPCExplicitTorqueVectoringPower();
  }
}
