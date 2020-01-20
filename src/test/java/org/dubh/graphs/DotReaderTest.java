package org.dubh.graphs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class DotReaderTest {
  @Test
  public void testSomething() throws Exception {
    String dotFile = "" +
        "digraph \"deps\" {\n" +
        "  \"foo\"         -> \"bar\";\n" +
        "  \"bar'\"        -> \"baz\";\n" +
        "}";
    DotReader<String> dotReader = DotReader.createStringDotReader(stringInputStream(dotFile));
    Graph<String> graph = dotReader.read();
  }

  private InputStream stringInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }
}