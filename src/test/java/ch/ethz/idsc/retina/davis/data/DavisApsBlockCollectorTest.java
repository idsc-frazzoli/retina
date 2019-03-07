// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class DavisApsBlockCollectorTest extends TestCase {
  public void testSimple() {
    ByteBuffer master = ByteBuffer.wrap(new byte[40]);
    assertTrue(master.hasRemaining());
    assertEquals(master.remaining(), 40);
    ByteBuffer client = ByteBuffer.wrap(new byte[10]);
    master.put(client);
    assertTrue(master.hasRemaining());
    assertEquals(master.remaining(), 30);
    client.position(0);
    master.put(client);
    assertTrue(master.hasRemaining());
    assertEquals(master.remaining(), 20);
    client.position(0);
    master.put(client);
    assertTrue(master.hasRemaining());
    assertEquals(master.remaining(), 10);
    client.position(0);
    master.put(client);
    assertFalse(master.hasRemaining());
    assertEquals(master.remaining(), 0);
  }

  public void testSome() {
    ByteBuffer master = ByteBuffer.wrap(new byte[40]);
    assertTrue(master.hasRemaining());
    assertEquals(master.remaining(), 40);
    ByteBuffer client = ByteBuffer.wrap(new byte[10]);
    master.put(client);
    assertTrue(master.hasRemaining());
    assertEquals(master.remaining(), 30);
    assertEquals(master.capacity(), 40);
    assertEquals(master.limit(), 40);
    master.position(0);
    assertEquals(master.remaining(), 40);
  }
}
