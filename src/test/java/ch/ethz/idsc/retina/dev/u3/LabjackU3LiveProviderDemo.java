// code by jph
package ch.ethz.idsc.retina.dev.u3;

enum LabjackU3LiveProviderDemo  {
  ;
  public static void main(String[] args) throws InterruptedException {
    LabjackU3LiveProvider labjackU3LiveProvider = new LabjackU3LiveProvider();
    labjackU3LiveProvider.start();
    Thread.sleep(10000);
    labjackU3LiveProvider.stop();
  }
}
