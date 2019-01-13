// code by jph
package ch.ethz.idsc.retina.u3;

/* package */ enum LabjackU3LiveProviderDemo implements LabjackAdcListener {
  INSTANCE;
  @Override
  public void labjackAdc(LabjackAdcFrame labjackAdcFrame) {
    System.out.println(labjackAdcFrame.asVector());
  }

  public static void main(String[] args) throws InterruptedException {
    LabjackU3LiveProvider labjackU3LiveProvider = new LabjackU3LiveProvider(LabjackU3Config.INSTANCE, INSTANCE);
    labjackU3LiveProvider.start();
    Thread.sleep(60000);
    labjackU3LiveProvider.stop();
  }
}
