// code by jph
package ch.ethz.idsc.demo.jph.can;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

/* package */ enum SteerCanParser {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file = HomeDirectory.file("active_operations.csv");
    Tensor tensor = Import.of(file);
    for (Tensor row : tensor) {
      // Thread.sleep(2);
      int id = row.Get(2).number().intValue();
      Tensor data = row.extract(3, row.length());
      row = null;
      switch (id) {
      case 1: {
        int value3 = data.Get(3).number().intValue();
        int value4 = data.Get(4).number().intValue();
        // if (false)
        System.out.println(String.format("01 = %6d %6d %6d %6d %6d", //
            toShort(data.extract(0, 2)), // ReqMotTrq : 0|16@1-
            data.Get(3).number().intValue(), // AboxMsgCntr : 16|8@1+
            value3 & 1, //
            value3, //
            value4));
        break;
      }
      case 10:
        // System.out.println(data);
        // if (false)
        System.out.println(String.format("10 = %6d %6d %6d %6d", //
            toShort(data.extract(0, 2)), // AngSpd : 0|16@1-
            toShort(data.extract(2, 4)), // TsuTrq : 16|16@1-
            toShort(data.extract(4, 6)), // RefMotTrq : 32|16@1-
            toShort(data.extract(6, 8)) // EstMotTrq : 48|16@1-
        ));
        break;
      case 11: {
        int value6 = data.Get(6).number().intValue();
        System.out.println(data);
        System.out.println(String.format("11 = %6d %6d %6d", //
            toInt24(data.extract(0, 3)), // RelIntRotAng : 0|24@1-
            value6));
        break;
      }
      default:
        break;
      }
    }
  }

  private static short toShort(Tensor tensor) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[2]);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    byteBuffer.put(tensor.Get(0).number().byteValue());
    byteBuffer.put(tensor.Get(1).number().byteValue());
    byteBuffer.position(0);
    return byteBuffer.getShort();
  }

  private static int toInt24(Tensor tensor) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    byteBuffer.put((byte) 0);
    byteBuffer.put(tensor.Get(0).number().byteValue());
    byteBuffer.put(tensor.Get(1).number().byteValue());
    byteBuffer.put(tensor.Get(2).number().byteValue());
    byteBuffer.position(0);
    return byteBuffer.getInt();
  }
}
