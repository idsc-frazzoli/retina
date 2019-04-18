// code by mh
package ch.ethz.idsc.demo.mh;

import ch.ethz.idsc.gokart.core.map.GenericBayesianMapping;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

/* package */ enum StartMapping {
  ;
  public static void main(String[] args) throws Exception {
    GenericBayesianMapping genericBayesianMapping = GenericBayesianMapping.createObstacleMapping();
    genericBayesianMapping.start();
    ModuleAuto.INSTANCE.runOne(PresenterLcmModule.class);
  }
}
