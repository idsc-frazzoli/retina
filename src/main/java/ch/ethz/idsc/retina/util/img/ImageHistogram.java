// code by jph
package ch.ethz.idsc.retina.util.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.data.GlobalAssert;

public enum ImageHistogram {
  ;
  public static int[] of(BufferedImage bufferedImage) {
    GlobalAssert.that(bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY);
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    int[] bins = new int[256];
    IntStream.range(0, bytes.length) //
        .map(i -> bytes[i] & 0xff) //
        .forEach(i -> ++bins[i]);
    return bins;
  }
}
