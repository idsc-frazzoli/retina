// code by jph
package ch.ethz.idsc.owl.car.demo;

import ch.ethz.idsc.owl.bot.se2.glc.GokartEntity;
import ch.ethz.idsc.owl.bot.se2.glc.GokartVecEntity;
import ch.ethz.idsc.owl.bot.se2.glc.HelperHangarMap;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** demo to simulate dubendorf hangar */
class GokartManualDemo implements DemoInterface {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    GokartEntity gokartEntity = new GokartVecEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 10));
      }
    };
    // ---
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180610.png", gokartEntity);
    // ---
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(hangarMap.region);
    MouseGoal.simple(owlyAnimationFrame, gokartEntity, plannerConstraint);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    // ---
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(hangarMap.region), //
          GokartEntity.SHAPE, () -> gokartEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new GokartManualDemo().start().jFrame.setVisible(true);
  }
}