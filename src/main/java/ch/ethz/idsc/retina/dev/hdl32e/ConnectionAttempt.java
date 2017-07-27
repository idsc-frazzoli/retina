// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.ShortUtils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

class ConnectionAttempt {
  /** Example Positioning Packet
   * 
   * {0.00, 3.91, -5.66}
   * {42.000, 43.017, 46.359}
   * {0.99, 0.99, 0.03}
   * {0.00, -0.01, -0.04}
   * $GPRMC,134214,A,4722.6885,N,00832.8762,E,000.1,104.4,200717,001.8,E,D*17
   * 
   * @param args
   * @throws Exception */
  public static void main(String[] args) throws Exception {
    // 2368
    try (DatagramSocket datagramSocket = new DatagramSocket(8308)) {
      byte[] bytes = new byte[10000];
      byte[] nmea = new byte[72];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      DatagramPacket datagramPacket = new DatagramPacket(bytes, 10000);
      int gyro1;
      int gyro2;
      int gyro3;
      while (true) {
        datagramSocket.receive(datagramPacket);
        // datagramPacket.getLength()
        // 1206 for laser
        // 512 for GPS
        /** information on p.21 of HDL-32E user's manual */
        byteBuffer.position(14); // first 14 not used
        gyro1 = ShortUtils._24bit(byteBuffer.getShort());
        int temp1 = ShortUtils._24bit(byteBuffer.getShort());
        int acce1X = ShortUtils._24bit(byteBuffer.getShort());
        int acce1Y = ShortUtils._24bit(byteBuffer.getShort());
        // ---
        gyro2 = ShortUtils._24bit(byteBuffer.getShort());
        int temp2 = ShortUtils._24bit(byteBuffer.getShort());
        int acce2X = ShortUtils._24bit(byteBuffer.getShort());
        int acce2Y = ShortUtils._24bit(byteBuffer.getShort());
        // ---
        gyro3 = ShortUtils._24bit(byteBuffer.getShort());
        int temp3 = ShortUtils._24bit(byteBuffer.getShort());
        int acce3X = ShortUtils._24bit(byteBuffer.getShort());
        int acce3Y = ShortUtils._24bit(byteBuffer.getShort());
        Tensor gyroRaw = Tensors.vector(gyro1, gyro2, gyro3);
        Tensor tempRaw = Tensors.vector(temp1, temp2, temp3);
        Tensor acceXRaw = Tensors.vector(acce1X, acce2X, acce3X);
        Tensor acceYRaw = Tensors.vector(acce1Y, acce2Y, acce3Y);
        // converting gyro
        System.out.println(gyroRaw.multiply(RealScalar.of(0.09766)).map(Round._2));
        // converting temperatur
        System.out.println(tempRaw //
            .map(s -> s.multiply(RealScalar.of(0.1453)).add(RealScalar.of(25)) //
                .map(Round._3)));
        System.out.println(acceXRaw.multiply(RealScalar.of(0.001221)).map(Round._2));
        System.out.println(acceYRaw.multiply(RealScalar.of(0.001221)).map(Round._2));
        byteBuffer.position(206);
        // NMEA=
        // $GPRMC,131653,A,4722.6848,N,00832.8727,E,000.1,276.2,200717,001.8,E,D*15
        byteBuffer.get(nmea);
        System.out.println(new String(nmea));
      }
    }
  }
}
