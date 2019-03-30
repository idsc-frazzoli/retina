// code by mh
package ch.ethz.idsc.demo.mh;

import ch.ethz.idsc.gokart.core.map.GenericBayesianMapping;
import ch.ethz.idsc.gokart.gui.top.PresenterLcmModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

/* package */ enum GokartMappingDemoStart {
  ;
  public static void main(String[] args) {
    // PresenterLcmModule presenterLcmModule = new PresenterLcmModule();
    GenericBayesianMapping gokartMappingModule = GenericBayesianMapping.createObstacleMapping();
    gokartMappingModule.start();
    ModuleAuto.INSTANCE.runOne(PresenterLcmModule.class);
  }
}
