// code by ynager
package ch.ethz.idsc.owl.car.demo;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.ethz.idsc.gokart.core.pure.TrajectoryConfig;
import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.bot.se2.glc.GlcWaypointFollowing;
import ch.ethz.idsc.owl.bot.se2.glc.GokartVecEntity;
import ch.ethz.idsc.owl.bot.se2.glc.HelperHangarMap;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** demo to simulate dubendorf hangar */
// TODO JPH what does this demo do?
class GokartWaypoint3Demo implements DemoInterface {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    final Tensor waypoints = TrajectoryConfig.getWaypoints();
    // final CostFunction waypointCost = WaypointDistanceCost.linear(waypoints, Tensors.vector(85.33, 85.33), 10.0f, new Dimension(640, 640));
    GokartVecEntity gokartEntity = new GokartVecEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 10));
      }
      // @Override
      // public Optional<CostFunction> getPrimaryCost() {
      // return Optional.of(waypointCost);
      // }
    };
    // ---
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180610.png", gokartEntity);
    // ---
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(hangarMap.region);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    // ---
    // RenderInterface renderInterface = ;
    owlyAnimationFrame.addBackground(new WaypointRender(Arrowhead.of(0.6), new Color(64, 192, 64, 64)).setWaypoints(waypoints));
    GlcPlannerCallback glcPlannerCallback = new SimpleGlcPlannerCallback(gokartEntity);
    GlcWaypointFollowing wpf = new GlcWaypointFollowing( //
        waypoints, RealScalar.of(2), gokartEntity, plannerConstraint, //
        Arrays.asList(gokartEntity, glcPlannerCallback));
    wpf.setHorizonDistance(RealScalar.of(7));
    wpf.startNonBlocking();
    // ---
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        System.out.println("window was closed. terminating...");
        wpf.flagShutdown();
      }
    });
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    // ---
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new GokartWaypoint3Demo().start().jFrame.setVisible(true);
  }
}