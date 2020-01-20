package org.dubh.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  public class KosarajuState {
    final Set<T> visited = new LinkedHashSet<>();
    final Map<T, Set<T>> components = new HashMap<>();
    final Map<T, T> componentToRoot = new HashMap<>();
    final Set<T> componentized = new LinkedHashSet<>();
    List<Node<T>> orderedNodes = new LinkedList<Node<T>>();

    boolean isVisited(Node<T> n) {
      return visited.contains(n.value);
    }

    void setVisited(Node<T> n) {
      visited.add(n.value);
    }

    void addComponent(Node<T> root, Node<T> item) {
      Set<T> nodes = components.get(root.value);
      if (nodes == null) {
        nodes = new LinkedHashSet<>();
        components.put(root.value, nodes);
      }
      nodes.add(item.value);
      componentized.add(item.value);
      componentToRoot.put(item.value, root.value);
    }

    boolean isComponentized(Node<T> n) {
      return componentized.contains(n.value);
    }

    public Map<T, Set<T>> getComponents() {
      return components;
    }

    // Returns the component graph, as a set of root -> dependee roots
    public Map<T, Set<T>> getComponentGraph() {
      Map<T, Set<T>> result = new HashMap<>();
      for (Map.Entry<T, Set<T>> entry : components.entrySet()) {
        T componentRoot = entry.getKey();
        Set<T> deps = result.get(componentRoot);
        if (deps == null) {
          deps = new LinkedHashSet<>();
          result.put(componentRoot, deps);
        }
        for (T componentInGroup : entry.getValue()) {
          Set<Node<T>> outGoingDeps = getNode(componentInGroup).uses;
          for (Node<T> n : outGoingDeps) {
            T depComponentRoot = componentToRoot.get(n.value);
            if (!depComponentRoot.equals(componentRoot)) {
              deps.add(depComponentRoot);
            }
          }

        }
      }
      return result;
    }
  }

  public KosarajuState kosaraju() {
    KosarajuState state = new KosarajuState();
    for (Node<T> n : valueToNode.values()) {
      kosarajuVisit(state, n);
    }

    for (Node<T> n : state.orderedNodes) {
      kosarajuAssign(state, n, n);
    }
    return state;
  }

  private void kosarajuAssign(KosarajuState state, Node<T> root, Node<T> n) {
    if (state.isComponentized(n))
      return;
    state.addComponent(root, n);
    for (Node<T> dep : n.usedBy) {
      kosarajuAssign(state, root, dep);
    }
  }

  private void kosarajuVisit(KosarajuState state, Node<T> n) {
    if (state.isVisited(n))
      return;
    state.setVisited(n);
    for (Node<T> dep : n.uses) {
      kosarajuVisit(state, dep);
    }
    state.orderedNodes.add(0, n);
  }
}