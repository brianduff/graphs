package org.dubh.graphs;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Node<T> {
  final T value;
  final Set<Node<T>> uses = new LinkedHashSet<>();
  final Set<Node<T>> usedBy = new LinkedHashSet<>();

  Node(T value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }
    Node<?> other = (Node<?>) o;
    return Objects.equals(value, other.value);
  }

  public T getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}