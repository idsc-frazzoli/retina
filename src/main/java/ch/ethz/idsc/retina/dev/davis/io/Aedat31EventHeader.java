// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;

/** https://inilabs.com/support/software/fileformat/#h.w7vjqzw55d5b */
public class Aedat31EventHeader {
  private final short eventType;
  private final short eventSource;
  private final int eventSize;
  private final int eventTSOffset;
  private final int eventTSOverflow;
  private final int eventCapacity;
  private final int eventNumber;
  private final int eventValid;

  public Aedat31EventHeader(ByteBuffer byteBuffer) {
    eventType = byteBuffer.getShort();
    eventSource = byteBuffer.getShort();
    eventSize = byteBuffer.getInt();
    eventTSOffset = byteBuffer.getInt();
    eventTSOverflow = byteBuffer.getInt();
    eventCapacity = byteBuffer.getInt();
    eventNumber = byteBuffer.getInt();
    eventValid = byteBuffer.get();
  }

  public Aedat31EventType getType() {
    return Aedat31EventType.values()[eventType];
  }

  public int getSize() {
    return eventSize * eventNumber;
  }

  public int getNumber() {
    return eventNumber;
  }

  public void printInfoLine() {
    System.out.println("eventType      : " + eventType);
    System.out.println("eventSource    : " + eventSource);
    System.out.println("eventSize      : " + eventSize);
    System.out.println("eventTSOffset  : " + eventTSOffset);
    System.out.println("eventTSOverflow: " + eventTSOverflow);
    System.out.println("eventCapacity  : " + eventCapacity);
    System.out.println("eventNumber    : " + eventNumber);
    System.out.println("eventValid     : " + eventValid);
    System.out.println("---");
  }
}
