// code by jph
package ch.ethz.idsc.retina.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bulletin implements Iterable<String> {
  private List<String> list = new LinkedList<>();

  public void append(String line) {
    Stream.of(line.split("\\n"))//
        .filter(string -> !string.isEmpty()) //
        .forEach(list::add);
  }

  @Override
  public String toString() {
    return list.stream().collect(Collectors.joining("\n"));
  }

  public int size() {
    return list.size();
  }

  public void clear() {
    list.clear();
  }

  @Override
  public Iterator<String> iterator() {
    return list.iterator();
  }
}
