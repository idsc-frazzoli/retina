// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

/** for the demo, the sensor has to be connected to the pc */
enum LiveUrgProviderDemo {
  ;
  public static void main(String[] args) throws Exception {
    UrgListener urgListener = new UrgListener() {
      @Override
      public void urg(String line) {
        System.out.println(line);
      }
    };
    LiveUrgProvider.INSTANCE.listeners.add(urgListener);
    LiveUrgProvider.INSTANCE.start();
    Thread.sleep(2500);
    LiveUrgProvider.INSTANCE.stop();
  }
}
