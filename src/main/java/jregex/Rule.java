package jregex;

import java.util.function.Function;

public interface Rule {
  boolean match(String s, Function<String, Boolean> cont);

  default boolean match(String s) {
    return match(s, cont -> true);
  }

  default Rule append(Rule other) {
    return (s, cont) -> match(s, rest -> other.match(rest, cont));
  }

  default Rule or(Rule other) {
    return (s, cont) -> match(s, cont) || other.match(s, cont);
  }
}