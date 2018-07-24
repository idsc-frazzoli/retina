// code by mg
package ch.ethz.idsc.demo.mg.slam.online;

import ch.ethz.idsc.retina.sys.AbstractModule;

public class SlamModule extends AbstractModule {
  private final OnlineSlamWrap onlineSlamWrap = new OnlineSlamWrap();

  @Override // from AbstractModule
  protected void first() throws Exception {
    onlineSlamWrap.start();
  }

  @Override // from AbstractModule
  protected void last() {
    onlineSlamWrap.stop();
  }

  // ---
  public static void standalone() throws Exception {
    SlamModule slamModule = new SlamModule();
    slamModule.first();
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
