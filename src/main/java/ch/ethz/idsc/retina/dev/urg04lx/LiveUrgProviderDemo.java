// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

/** for the demo, the sensor has to be connected to the pc */
enum LiveUrgProviderDemo {
  ;
  public static void main(String[] args) throws Exception {
    LiveUrgProvider.INSTANCE.addListener(System.out::println);
    LiveUrgProvider.INSTANCE.start();
    Thread.sleep(2500);
    LiveUrgProvider.INSTANCE.stop();
  }
}
