// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;

/** executes the mapping step of the SLAM algorithm for the case that the pose is provided from another module,
 * e.g. lidar or odometry */
/* package */ class SlamMappingStep extends EventActionSlamStep {
  protected SlamMappingStep(SlamContainer slamContainer) {
    super(slamContainer);
  }

  @Override // from EventActionSlamStep
  void davisDvsAction() {
    double[] eventGokartFrame = slamContainer.getEventGokartFrame();
    if (Objects.nonNull(eventGokartFrame))
      SlamMappingStepUtil.updateOccurrenceMap( //
          slamContainer.getPoseUnitless(), //
          slamContainer.getOccurrenceMap(), //
          eventGokartFrame);
  }
}
