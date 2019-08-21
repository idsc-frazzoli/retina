// code by jph
package ch.ethz.idsc.demo.mp.playground;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.glc.CarPolicyEntity;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class RLDemo implements DemoInterface {
  @Override
  public BaseFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._0F5C_2182;
    Region<Tensor> region = r2ImageRegionWrap.region();
    TrajectoryRegionQuery trajectoryRegionQuery = CatchyTrajectoryRegionQuery.timeInvariant(region);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
    Tensor startPos = Tensors.vector(5.117, 5.950, 0.000);
    CarPolicyEntity carPolicyEntity = //
        new CarPolicyEntity(startPos, SarsaType.ORIGINAL, trajectoryRegionQuery);
    owlyAnimationFrame.add(carPolicyEntity);
    CarPolicyEntity carPolicyEntity2 = //
        new CarPolicyEntity(startPos, SarsaType.EXPECTED, trajectoryRegionQuery);
    owlyAnimationFrame.add(carPolicyEntity2);
    CarPolicyEntity carPolicyEntity3 = //
        new CarPolicyEntity(startPos, SarsaType.QLEARNING, trajectoryRegionQuery);
    owlyAnimationFrame.add(carPolicyEntity3);
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new RLDemo().start().jFrame.setVisible(true);
  }
}
