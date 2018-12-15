// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.config.DavisSlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.config.DavisSlamPrcConfig;
import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.gokart.core.joy.TorqueVectoringConfig;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.mpc.MPCOptimizationConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pure.PlanSRConfig;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;
import ch.ethz.idsc.gokart.core.pure.TrajectoryConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.dev.linmot.LinmotConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;

enum ParametersHelper {
  ;
  public static final List<Object> OBJECTS = Arrays.asList(//
      ChassisGeometry.GLOBAL, //
      SensorsConfig.GLOBAL, //
      LinmotConfig.GLOBAL, //
      SteerConfig.GLOBAL, //
      RimoConfig.GLOBAL, //
      SafetyConfig.GLOBAL, //
      LocalizationConfig.GLOBAL, //
      ManualConfig.GLOBAL, //
      PursuitConfig.GLOBAL, //
      TorqueVectoringConfig.GLOBAL, //
      MPCOptimizationConfig.GLOBAL, //
      ClusterConfig.GLOBAL, //
      TrajectoryConfig.GLOBAL, //
      PlanSRConfig.GLOBAL, //
      MappingConfig.GLOBAL, //
      DavisSlamPrcConfig.GLOBAL, //
      DavisSlamCoreConfig.GLOBAL);
}
