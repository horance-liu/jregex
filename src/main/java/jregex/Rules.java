package jregex;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class Rules {
  private Rules() {
  }

  public static Rule any() {
    return atom(c -> true);
  }

  public static Rule oneOf(String range) {
    return atom(c -> range.indexOf(c) != -1);
  }

  public static Rule val(int v) {
    return atom(c -> c == v);
  }

  public static Rule val(String v) {
    Rule init = (s, cont) -> v.isEmpty() ? s.isEmpty() : true;
    return v.chars()
            .mapToObj(Rules::val)
            .reduce(init, Rule::append);
  }


  private static Rule atom(Predicate<Integer> spec) {
    return (str, cont) -> {
      if (str.isEmpty())
        return false;
      else
        return spec.test(str.codePointAt(0))
            && cont.apply(str.substring(1));
    };
  }

  public static Rule optional(Rule rule) {
    return (s, cont) -> rule.match(s, cont)
                     || cont.apply(s);
  }

  public static Rule many(Rule rule) {
    return (s, cont) -> rule.match(s, rest -> many(rule).match(rest, cont))
                     || cont.apply(s);
  }

  public static Rule oneOrMore(Rule rule) {
    return rule.append(many(rule));
  }

  public static Rule eof(Rule rule) {
    Rule end = (s, cond) -> s.isEmpty();
    return rule.append(end);
  }

  public static Rule sequence(Rule init, Rule... rules) {
    return concat(Rule::append, init, rules);
  }

  public static Rule alternative(Rule init, Rule... rules) {
    return concat(Rule::or, init, rules);
  }

  private static Rule concat(BiFunction<Rule, Rule, Rule> bf,
                             Rule init, Rule[] rules) {
    return Arrays.stream(rules)
            .reduce(init, (r1, r2) -> bf.apply(r1, r2));
  }
}
