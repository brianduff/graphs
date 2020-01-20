package org.dubh.graphs;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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