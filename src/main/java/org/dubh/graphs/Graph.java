package org.dubh.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Graph<T> {
  private final Map<T, Node<T>> valueToNode = new HashMap<>();
  
  private Node<T> getOrCreateNode(T value) {
    Node<T> node = valueToNode.get(value);
    if (node == null) {
      node = new Node<>(value);
      valueToNode.put(value, node);
    }
    return node;
  }

  private Collection<Node<T>> getNodes(Collection<T> values) {
    Collection<Node<T>> nodes = new LinkedHashSet<>(values.size());
    for (T s : values) {
      nodes.add(getOrCreateNode(s));
    }
    return nodes;
  }

  public void add(T name, Collection<T> uses) {
    Node<T> theNode = getOrCreateNode(name);
    theNode.uses.addAll(getNodes(uses));
    for (T s : uses) {
      getOrCreateNode(s).usedBy.add(theNode);
    }
  }
}