// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.urg04lx.LiveUrg04lxProvider;

/** for the demo, the sensor has to be connected to the pc */
enum LiveUrgProviderDemo {
  ;
  public static void main(String[] args) throws Exception {
    LiveUrg04lxProvider.INSTANCE.addListener(System.out::println);
    LiveUrg04lxProvider.INSTANCE.start();
    Thread.sleep(2500);
    LiveUrg04lxProvider.INSTANCE.stop();
  }
}
