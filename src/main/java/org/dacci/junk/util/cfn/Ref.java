package org.dacci.junk.util.cfn;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ref implements List<Void> {
  private String reference;

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public Iterator<Void> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Void e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Void> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends Void> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {}

  @Override
  public Void get(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Void set(int index, Void element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, Void element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Void remove(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(Object o) {
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    return -1;
  }

  @Override
  public ListIterator<Void> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<Void> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Void> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }
}
