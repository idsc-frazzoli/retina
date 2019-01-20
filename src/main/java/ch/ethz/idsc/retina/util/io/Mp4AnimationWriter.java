// adapted from io.humble
// code by jph
package ch.ethz.idsc.retina.util.io;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

public class Mp4AnimationWriter implements AnimationWriter {
  private final Muxer muxer;
  private final Encoder encoder;
  private MediaPictureConverter converter = null;
  private final MediaPicture picture;
  private final MediaPacket packet;

  public Mp4AnimationWriter(String filename, Dimension dimension, int snapsPerSecond) //
      throws InterruptedException, IOException {
    final Rectangle screenbounds = new Rectangle(dimension);
    final Rational framerate = Rational.make(1, snapsPerSecond);
    /** First we create a muxer using the passed in filename and formatname if given. */
    muxer = Muxer.make(filename, null, null);
    /** Now, we need to decide what type of codec to use to encode video. Muxers
     * have limited sets of codecs they can use. We're going to pick the first one that
     * works, or if the user supplied a codec name, we're going to force-fit that
     * in instead. */
    final MuxerFormat format = muxer.getFormat();
    System.out.println(format);
    final Codec codec;
    // if (codecname != null) {
    // codec = Codec.findEncodingCodecByName(codecname);
    // } else {
    codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
    // }
    System.out.println(codec);
    /** Now that we know what codec, we need to create an encoder */
    encoder = Encoder.make(codec);
    /** Video encoders need to know at a minimum: width height pixel format
     * Some also need to know frame-rate (older codecs that had a fixed rate at which video files could
     * be written needed this). There are many other options you can set on an encoder, but we're
     * going to keep it simpler here. */
    encoder.setWidth(screenbounds.width);
    encoder.setHeight(screenbounds.height);
    // We are going to use 420P as the format because that's what most video formats these days use
    final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
    encoder.setPixelFormat(pixelformat);
    encoder.setTimeBase(framerate);
    /** An annoyance of some formats is that they need global (rather than per-stream) headers,
     * and in that case you have to tell the encoder. And since Encoders are decoupled from
     * Muxers, there is no easy way to know this beyond */
    if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
      encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
    /** Open the encoder. */
    encoder.open(null, null);
    /** Add this stream to the muxer. */
    muxer.addNewStream(encoder);
    /** And open the muxer for business. */
    muxer.open(null, null);
    /** Next, we need to make sure we have the right MediaPicture format objects
     * to encode data with. Java (and most on-screen graphics programs) use some
     * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
     * codecs use some variant of YCrCb formatting. So we're going to have to
     * convert. To do that, we'll introduce a MediaPictureConverter object later. */
    picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
    picture.setTimeBase(framerate);
    /** Now begin our main loop of taking screen snaps.
     * We're going to encode and then write out any resulting packets. */
    packet = MediaPacket.make();
  }

  int count = 0;

  @Override // from AnimationWriter
  public void append(BufferedImage bufferedImage) {
    /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
    if (Objects.isNull(converter))
      converter = MediaPictureConverterFactory.createConverter(bufferedImage, picture);
    converter.toPicture(picture, bufferedImage, count);
    ++count;
    push(picture);
  }

  @Override // from AnimationWriter
  public void append(Tensor tensor) {
    throw new UnsupportedOperationException();
  }

  @Override // from AutoCloseable
  public void close() throws Exception {
    /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
     * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
     * input until the output is not complete. */
    push(null);
    /** Finally, let's clean up after ourselves. */
    muxer.close();
  }

  private void push(MediaPicture picture) {
    do {
      encoder.encode(packet, picture);
      if (packet.isComplete())
        muxer.write(packet, false);
    } while (packet.isComplete());
  }
}
