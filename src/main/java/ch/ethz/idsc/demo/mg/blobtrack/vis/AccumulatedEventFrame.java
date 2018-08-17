// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.util.vis.VisPipelineUtil;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.ImageRotate;

/** BufferedImage with accumulated events and overlaid ImageBlobs */
public class AccumulatedEventFrame {
  private static final byte[] VALUE = { 0, (byte) 255 };
  // ---
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final ImageCopy imageCopy; // for correct visualization
  private final byte[] bytes;
  private final boolean rotateFrame;
  private final int width;
  private final int height;

  public AccumulatedEventFrame(BlobTrackConfig blobTrackConfig) {
    width = blobTrackConfig.davisConfig.width.number().intValue();
    height = blobTrackConfig.davisConfig.height.number().intValue();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
    graphics = bufferedImage.createGraphics();
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    imageCopy = new ImageCopy();
    rotateFrame = blobTrackConfig.rotateFrame;
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = (byte) 240);
  }

  public BufferedImage getAccumulatedEvents() {
    return getFrame();
  }

  /** overlays accumulatedEventFrame with active ImageBlobs
   * 
   * @param activeBlobs list of active ImageBlob objects
   * @param selectedBlobColor color for selected blobs
   * @param rejectedBlobColor color for rejected blobs
   * @return BufferedImage for visualization */
  public BufferedImage overlayActiveBlobs(List<ImageBlob> activeBlobs, Color selectedBlobColor, Color rejectedBlobColor) {
    for (int i = 0; i < activeBlobs.size(); i++) {
      if (activeBlobs.get(i).getIsRecognized()) {
        VisPipelineUtil.drawImageBlob(graphics, activeBlobs.get(i), selectedBlobColor);
      } else {
        VisPipelineUtil.drawImageBlob(graphics, activeBlobs.get(i), rejectedBlobColor);
      }
    }
    return getFrame();
  }

  /** overlays accumulatedEventFrame with hidden ImageBlobs
   * 
   * @param hiddenBlobs list of hidden ImageBlob objects
   * @param blobColor color for blobs
   * @return BufferedImage for visualization */
  public BufferedImage overlayHiddenBlobs(List<ImageBlob> hiddenBlobs, Color blobColor) {
    for (int i = 0; i < hiddenBlobs.size(); i++) {
      VisPipelineUtil.drawImageBlob(graphics, hiddenBlobs.get(i), blobColor);
    }
    return getFrame();
  }

  // marks the event in the image plane as a dark or light pixel
  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    int index = davisDvsEvent.x + davisDvsEvent.y * width;
    bytes[index] = VALUE[davisDvsEvent.i];
  }

  // depending on whether frame is rotated or not
  private BufferedImage getFrame() {
    if (rotateFrame)
      return ImageRotate._180deg(bufferedImage);
    imageCopy.update(bufferedImage);
    return imageCopy.get();
  }

  public byte[] getBytes() {
    return bytes;
  }
}
