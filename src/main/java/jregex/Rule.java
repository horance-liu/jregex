package jregex;

import java.util.function.Predicate;

public interface Rule {
  boolean match(String s, Predicate<String> cont);

  default boolean match(String s) {
    return match(s, rest -> true);
  }

  default Rule append(Rule other) {
    return (s, cont) -> match(s, rest -> other.match(rest, cont));
  }

  default Rule or(Rule other) {
    return (s, cont) -> match(s, cont) || other.match(s, cont);
  }
}
