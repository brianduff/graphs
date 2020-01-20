package org.dubh.graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Very simplistic .dot file reader. Assumes the file contains a single digraph.
 */
public class DotReader<T> {
  private final InputStream inputStream;
  private final Function<String, T> valueConverter;
  private Optional<Predicate<T>> filter = Optional.empty();

  public DotReader(InputStream inputStream, Function<String, T> valueConverter) {
    this.inputStream = inputStream;
    this.valueConverter = valueConverter;
  }

  /**
   * Creates a new reader that uses String values.
   */
  public static DotReader<String> createStringDotReader(InputStream inputStream) {
    return new DotReader<String>(inputStream, value -> value);
  }

  public DotReader<T> setFilter(Predicate<T> filter) {
    this.filter = Optional.of(filter);
    return this;
  }

  /**
   * Read the file to a Graph.
   * @return
   * @throws IOException
   */
  public Graph<T> read() throws IOException {
    Graph<T> graph = new Graph<>();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      br.lines().forEach(line -> {
        String[] depParts = line.split("->");
        if (depParts.length != 2) return;

        String from = unquote(depParts[0].trim());
        String to = unquote(removeTrailing(depParts[1].trim(), ';'));

        T fromValue = valueConverter.apply(from);
        T toValue = valueConverter.apply(to);

        if (filter.orElse(x -> true).test(fromValue) && filter.orElse(x -> true).test(toValue)) {
          graph.add(valueConverter.apply(from), Collections.singleton(valueConverter.apply(to)));
        }
      });
    }
    return graph;
  }

  /** Strip a single leading / trailing quote from string. */
  private static String unquote(String s) {
    return removeLeading(removeTrailing(s, '"'), '"');
  }

  private static String removeTrailing(String s, char c) {
    if (s.charAt(s.length() - 1) == c) {
      return s.substring(0, s.length() - 1);
    }
    return s;
  }

  private static String removeLeading(String s, char c) {
    if (s.charAt(0) == c) {
      return s.substring(1);
    }
    return s;
  }
}