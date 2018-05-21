// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.ImageRotate;

// provides BufferedImage with accumulated events and overlaid ImageBlobs
public class AccumulatedEventFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private static final byte[] VALUE = { 0, (byte) 255 };
  private static int width;
  private static int height;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final byte[] bytes;
  private final ImageCopy imageCopy; // for correct visualization
  private final boolean rotateFrame;

  public AccumulatedEventFrame(PipelineConfig pipelineConfig) {
    width = pipelineConfig.width.number().intValue();
    height = pipelineConfig.height.number().intValue();
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
    graphics = bufferedImage.createGraphics();
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    imageCopy = new ImageCopy();
    rotateFrame = pipelineConfig.rotateFrame;
    clearImage();
  }

  /** displays the accumulated events
   * 
   * @return BufferedImage for visualization */
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
        VisualizationUtil.drawImageBlob(graphics, activeBlobs.get(i), selectedBlobColor);
      } else {
        VisualizationUtil.drawImageBlob(graphics, activeBlobs.get(i), rejectedBlobColor);
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
      VisualizationUtil.drawImageBlob(graphics, hiddenBlobs.get(i), blobColor);
    }
    return getFrame();
  }

  // marks the event in the image plane as a dark or light pixel
  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    int index = davisDvsEvent.x + davisDvsEvent.y * width;
    bytes[index] = VALUE[davisDvsEvent.i];
  }

  // resets all pixel to grey
  public void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }

  // depending on whether frame is rotated or not
  private BufferedImage getFrame() {
    if (rotateFrame) {
      return ImageRotate._180deg(bufferedImage);
    } else {
      imageCopy.update(bufferedImage);
      return imageCopy.get();
    }
  }
}
