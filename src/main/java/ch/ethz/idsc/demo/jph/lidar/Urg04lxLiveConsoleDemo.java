// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxLiveProvider;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;

/** for the demo, the sensor has to be connected to the pc */
enum Urg04lxLiveConsoleDemo {
  ;
  // accepted 1374 at 146878809 1504461075001
  // accepted 1374 at 146878908 1504461075100
  // accepted 1374 at 146879009 1504461075201
  // accepted 1374 at 146879109 1504461075302
  // accepted 1374 at 146879209 1504461075402
  // accepted 1374 at 146879309 1504461075501
  // accepted 1374 at 146879410 1504461075603
  // accepted 1374 at 146879510 1504461075703
  // accepted 1374 at 146879610 1504461075802
  public static void main(String[] args) throws Exception {
    Urg04lxLiveProvider.INSTANCE.addListener(new ByteArrayConsumer() {
      @Override
      public void accept(byte[] data, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.getShort();
        long timestamp = byteBuffer.getLong();
        System.out.println("accepted " + length + " at " + timestamp + " " + System.currentTimeMillis());
      }
    });
    Urg04lxLiveProvider.INSTANCE.start();
    Thread.sleep(5000);
    Urg04lxLiveProvider.INSTANCE.stop();
    System.out.println("finish");
  }
}
