// code by jph
package ch.ethz.idsc.retina.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.RandomAccess;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntRange implements Comparable<IntRange>, Iterable<Integer>, Serializable {
  public static final IntRange coverAll = new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
  /** inclusive */
  public final int min;
  /** exclusive */
  public final int max;

  public IntRange(int max) {
    this(0, max);
  }

  public IntRange(int min, int max) {
    assert min <= max;
    if (max < min)
      throw new RuntimeException("min=" + min + " gt max=" + max);
    this.min = min;
    this.max = max;
  }

  /** any subclass has to override create(...)
   * 
   * @param min
   * @param max
   * @return */
  @SuppressWarnings("unchecked")
  protected <Type extends IntRange> Type create(int min, int max) {
    return (Type) new IntRange(min, max);
  }

  public final <Type extends IntRange> Type withMin(int min) {
    return create(min, max);
  }

  public final <Type extends IntRange> Type withMax(int max) {
    return create(min, max);
  }

  /** @param value
   * @return min <= value && value < max */
  public final boolean contains(int value) {
    return min <= value && value < max;
  }

  @Deprecated
  final <Type> boolean contains(Entry<Integer, Type> myEntry) {
    return contains(myEntry.getKey());
  }

  public final int getWidth() {
    return max - min;
  }

  public final boolean isEmpty() {
    return getWidth() == 0;
  }

  public final boolean nonEmpty() {
    return !isEmpty();
  }

  public final int distanceFrom(int value) {
    if (max - 1 < value)
      return value - max + 1;
    if (value < min)
      return min - value;
    return 0;
  }

  /** @param value
   * @return Math.min(Math.max(min, value), max) */
  final int getMinMax(int value) {
    if (value < min)
      return min;
    return max < value ? max : value;
  }

  public final int getMinMax2(int value) {
    if (isEmpty())
      throw new RuntimeException("empty");
    if (value < min)
      return min;
    int last = max - 1;
    return last < value ? last : value;
  }

  public final <Type extends IntRange> Type translate(int delta) {
    return create(min + delta, max + delta);
  }

  public final <Type extends IntRange> Type shear(int delta_min, int delta_max) {
    return create(min + delta_min, max + delta_max);
  }

  public final <Type extends IntRange> Type intersect(IntRange myIntRange) {
    return intersect(myIntRange.min, myIntRange.max);
  }

  public final <Type extends IntRange> Type intersect(int _min, int _max) {
    _min = Math.max(_min, min);
    _max = Math.min(_max, max);
    return _min <= _max ? create(_min, _max) : create(min, min);
  }

  /** @param myIntRange
   * @return smallest interval covering this and myIntRange */
  public final <Type extends IntRange> Type cover(IntRange myIntRange) {
    return create(Math.min(min, myIntRange.min), Math.max(max, myIntRange.max));
  }

  /** only unions of intervals that overlap constitute a new interval
   * 
   * @param myIntRange
   * @return */
  public final boolean isUnionable(IntRange myIntRange) {
    return !intersect(myIntRange).isEmpty() || min == myIntRange.max || max == myIntRange.min;
  }

  public final <Type extends IntRange> Type union(IntRange myIntRange) {
    if (!isUnionable(myIntRange))
      throw new RuntimeException(this + " union " + myIntRange);
    return cover(myIntRange);
  }

  public SortedSet<Integer> capSet(SortedSet<Integer> mySortedSet) {
    return mySortedSet.subSet(min, max);
  }

  public <Type> SortedMap<Integer, Type> capMap(SortedMap<Integer, Type> mySortedMap) {
    return mySortedMap.subMap(min, max);
  }

  public IntStream stream() {
    return IntStream.range(min, max);
  }

  /** @return {@link RandomAccess} */
  public List<Integer> toList() {
    return stream().boxed().collect(Collectors.toList());
  }

  @Override
  public final int compareTo(IntRange myIntRange) {
    int cmp = Integer.compare(min, myIntRange.min);
    return cmp != 0 ? cmp : Integer.compare(max, myIntRange.max);
  }

  @Override
  public final Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      int count = min;

      @Override
      public boolean hasNext() {
        return count < max;
      }

      @Override
      public Integer next() {
        return count++;
      }
    };
  }

  public final Iterable<Integer> reversed() {
    return new Iterable<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          int count = max;

          @Override
          public boolean hasNext() {
            return min < count;
          }

          @Override
          public Integer next() {
            return --count;
          }
        };
      }
    };
  }

  @Override
  public final String toString() {
    return String.format("[%d,%d)", min, max);
  }

  @Override
  public final boolean equals(Object myObject) {
    IntRange myIntRange = (IntRange) myObject;
    return (min == myIntRange.min && max == myIntRange.max);
  }

  @Override
  public final int hashCode() {
    return min + max;
  }

  public static IntRange single(int value) {
    return new IntRange(value, value + 1);
  }

  public IntRange getMaxInclusive() {
    return new IntRange(min, max + 1);
  }

  public static IntRange fromString(String myString) {
    boolean valid = true;
    valid &= myString.charAt(0) == '[';
    int index = myString.indexOf(',');
    valid &= 0 < index;
    int last = myString.length() - 1;
    valid &= myString.charAt(last) == ')';
    if (valid)
      return new IntRange( //
          Integer.parseInt(myString.substring(1, index)), //
          Integer.parseInt(myString.substring(index + 1, last)));
    throw new NumberFormatException();
  }
}
