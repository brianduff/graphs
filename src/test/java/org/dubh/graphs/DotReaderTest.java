package org.dubh.graphs;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Test;

public class DotReaderTest {
  @Test
  public void testDfs() throws Exception {
    String dotFile = generateDot(
      "bar", "baz",
      "foo", "bar"
    );
    DotReader<String> dotReader = DotReader.createStringDotReader(stringInputStream(dotFile));
    Graph<String> graph = dotReader.read();
    List<String> result = new ArrayList<>();
    graph.depthFirstSearch("foo", n -> result.add(n));
    assertEquals(Arrays.asList("foo", "bar", "baz"), result);
  }

  @Test
  public void testComponents() throws Exception {
    String dotFile = generateDot(
      "a", "b",
      "b", "c",
      "c", "b",
      "d", "c"
    );
    DotReader<String> dotReader = DotReader.createStringDotReader(stringInputStream(dotFile));
    Graph<String> graph = dotReader.read();
    System.out.println(graph.kosaraju().getComponentGraph());
  }

  private class Package {
    private final String packageName;
    private final String jarName;

    Package(String s) {
      String[] parts = s.split(" ");
      if (parts.length < 2) {
        this.packageName = s;
        this.jarName = "";
      } else {
        this.packageName = parts[0];
        this.jarName = parts[1];
      }
    }

    @Override
    public String toString() {
      return packageName + " " + jarName;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(packageName);
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Package)) return false;
      return ((Package) o).packageName.equals(packageName);
    }
  }

  @Test
  public void testThing() throws Exception {
    DotReader<Package> dotReader = new DotReader<Package>(new FileInputStream("/tmp/deps/richdocument.filtered.dot"), s -> new Package(s));
    Graph<Package> graph = dotReader.read();
    Graph<Package>.KosarajuState state = graph.kosaraju();

    System.out.println("COMPONENTS");
    for (Map.Entry<Package, Set<Package>> entry : state.getComponents().entrySet()) {
      System.out.println(entry.getKey());
      for (Package s : entry.getValue()) {
        System.out.println("   " + s);
      }
    }

    System.out.println("=========\nCOMPONENT GRAPH");
    for (Map.Entry<Package, Set<Package>> entry : state.getComponentGraph().entrySet()) {
      System.out.println(entry.getKey());
      for (Package s : entry.getValue()) {
        System.out.println("   " + s);
      }
    }
  }

  private String generateDot(String... deps) {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph \"deps\" {\n");
    for (int i = 0; i < deps.length; i+=2) {
      sb.append("  \"" + deps[i] + "\"   -> \"" + deps[i+1] + "\";\n");
    }
    sb.append("}\n");
    return sb.toString();
  }

  private InputStream stringInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }
}