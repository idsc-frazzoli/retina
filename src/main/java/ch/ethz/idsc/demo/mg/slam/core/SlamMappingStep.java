// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;

/** executes the mapping step of the SLAM algorithm for the case that the pose is provided from another module,
 * e.g. lidar or odometry */
/* package */ class SlamMappingStep extends EventActionSlamStep {
  protected SlamMappingStep(SlamCoreContainer slamContainer) {
    super(slamContainer);
  }

  @Override // from EventActionSlamStep
  void davisDvsAction() {
    double[] eventGokartFrame = slamCoreContainer.getEventGokartFrame();
    if (Objects.nonNull(eventGokartFrame))
      SlamMappingStepUtil.updateOccurrenceMap( //
          slamCoreContainer.getPoseUnitless(), //
          slamCoreContainer.getOccurrenceMap(), //
          eventGokartFrame);
  }
}
