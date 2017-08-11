// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import ch.ethz.idsc.retina.urg04lxug01.Urg04lxLiveProvider;

/** for the demo, the sensor has to be connected to the pc */
enum Urg04lxLiveConsoleDemo {
  ;
  public static void main(String[] args) throws Exception {
    Urg04lxLiveProvider.INSTANCE.addListener(System.out::println);
    Urg04lxLiveProvider.INSTANCE.start();
    Thread.sleep(2500);
    Urg04lxLiveProvider.INSTANCE.stop();
  }
}
