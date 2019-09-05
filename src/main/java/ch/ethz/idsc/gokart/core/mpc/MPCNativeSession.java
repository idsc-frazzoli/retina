// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** the session counts messages according to their IDs */
/* package */ class MPCNativeSession {
  private final Map<MessageType, Integer> map = new EnumMap<>(MessageType.class);

  /** @param messageType
   * @return unique ID for given messageType starting at 0 */
  int nextMessageId(MessageType messageType) {
    Integer current = map.get(messageType);
    if (Objects.isNull(current))
      current = 0;
    map.put(messageType, current + 1);
    return current;
  }
}
