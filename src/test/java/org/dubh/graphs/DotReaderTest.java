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
  public void testSomething() throws Exception {
    String dotFile = "" +
        "digraph \"deps\" {\n" +
        "  \"bar\"         -> \"baz\";\n" +
        "  \"foo\"         -> \"bar\";\n" +
        "}";
    DotReader<String> dotReader = DotReader.createStringDotReader(stringInputStream(dotFile));
    Graph<String> graph = dotReader.read();
    List<String> result = new ArrayList<>();
    graph.depthFirstSearch("foo", n -> result.add(n));
    assertEquals(Arrays.asList("foo", "bar", "baz"), result);
  }

  private InputStream stringInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }
}