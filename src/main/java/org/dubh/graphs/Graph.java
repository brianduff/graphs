package org.dubh.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Consumer;

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

  private Node<T> getNode(T value) {
    Node<T> node = valueToNode.get(value);
    if (node == null) {
      throw new NullPointerException("No such value: " + value);
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

  public void depthFirstSearch(T startNode, Consumer<T> visitor) {
    DfsState state = new DfsState(visitor);
    dfsVisit(state, getNode(startNode));
  }

  public DfsState depthFirstSearch(Consumer<T> visitor) {
    DfsState state = new DfsState(visitor);
    valueToNode.values().stream()
        .filter(n -> state.isUnvisited(n))
        .forEach(n -> dfsVisit(state, n));
    return state;
  }

  private void dfsVisit(DfsState state, Node<T> node) {
    state.time++;
    state.getData(node).color = Color.GRAY;
    state.visitor.accept(node.value);
    node.uses.stream().filter(dep -> state.isUnvisited(dep)).forEach(dep -> {
      state.getData(dep).predecessor = node;
      dfsVisit(state, dep);
    });
    state.getData(node).color = Color.BLACK;
    state.time++;
  }


  private class DfsState {
    private final Map<T, DfsData> dfsData = new HashMap<>();
    private int time = 0;
    private final Consumer<T> visitor;

    DfsState(Consumer<T> visitor) {
      this.visitor = visitor;
      for (T value : valueToNode.keySet()) {
        dfsData.put(value, new DfsData());
      }
    }

    private boolean isUnvisited(Node<T> node) {
      return dfsData.get(node.value).color == Color.WHITE;
    }

    DfsData getData(Node<T> n) {
      DfsData data = dfsData.get(n.value);
      if (data == null)
        throw new NullPointerException("No node for " + n.value + " Graph is: " + dfsData);
      return data;
    }
  }

  private enum Color {
    GRAY, WHITE, BLACK;
  }

  private class DfsData {
    private Color color = Color.WHITE;
    private Node<T> predecessor;

    @Override
    public String toString() {
      return "DfsData[color=" + color + " predecessor=" + predecessor + "]";
    }
  }
}