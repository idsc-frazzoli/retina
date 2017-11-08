// code by jph
package ch.ethz.idsc.retina.util.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.stream.IntStream;

public enum ImageDifference {
  ;
  public static BufferedImage of(BufferedImage next, BufferedImage prev) {
    // 0.063196519 sec
    // Tensor sig = ImageFormat.from(a);
    // Tensor rst = ImageFormat.from(b);
    // return ImageFormat.of(sig.subtract(rst).map(Max.function(RealScalar.ZERO)));
    // 2.93964E-4 => 214.98 x faster!
    BufferedImage post = new BufferedImage(next.getWidth(), next.getHeight(), next.getType());
    byte[] _a = ((DataBufferByte) next.getRaster().getDataBuffer()).getData();
    byte[] _b = ((DataBufferByte) prev.getRaster().getDataBuffer()).getData();
    byte[] _c = ((DataBufferByte) post.getRaster().getDataBuffer()).getData();
    IntStream.range(0, _c.length) //
        .forEach(i -> _c[i] = (byte) Math.max(0, (_a[i] & 0xff) - (_b[i] & 0xff)));
    return post;
  }

  public static BufferedImage amplified(BufferedImage next, BufferedImage prev) {
    // 0.063196519 sec
    // Tensor sig = ImageFormat.from(a);
    // Tensor rst = ImageFormat.from(b);
    // return ImageFormat.of(sig.subtract(rst).map(Max.function(RealScalar.ZERO)));
    // 2.93964E-4 => 214.98 x faster!
    BufferedImage post = new BufferedImage(next.getWidth(), next.getHeight(), next.getType());
    byte[] _a = ((DataBufferByte) next.getRaster().getDataBuffer()).getData();
    byte[] _b = ((DataBufferByte) prev.getRaster().getDataBuffer()).getData();
    byte[] _c = ((DataBufferByte) post.getRaster().getDataBuffer()).getData();
    IntStream.range(0, _c.length) //
        .forEach(i -> _c[i] = (byte) Math.min(Math.max(0, ((_a[i] & 0xff) - (_b[i] & 0xff)) << 1), 255));
    return post;
  }
}
