package jregex;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class Rules {
  private Rules() {
  }

  public static Rule any() {
    return atom(s -> true, 1);
  }

  public static Rule oneOf(String range) {
    return atom(s -> range.contains(s), 1);
  }

  public static Rule val(String value) {
    return atom(s -> value.equals(s), value.length());
  }

  private static Rule atom(Predicate<String> spec, int length) {
    return (str, cont) -> {
      if (length == 0) return str.isEmpty();
      else if (str.length() < length) return false;
      else return spec.test(str.substring(0, length))
            && cont.apply(str.substring(length));
    };
  }

  public static Rule optional(Rule rule) {
    return (s, cont) -> rule.match(s, cont) || cont.apply(s);
  }

  public static Rule many(Rule rule) {
    return (s, cont) ->
        rule.match(s, rest -> many(rule).match(rest, cont)) || cont.apply(s);
  }

  public static Rule oneOrMore(Rule rule) {
    return rule.append(many(rule));
  }

  public static Rule eof(Rule rule) {
    Rule end = (s, cond) -> s.isEmpty();
    return rule.append(end);
  }

  public static Rule sequence(Rule first, Rule second, Rule... rules) {
    return concat(Rule::append, first, second, rules);
  }

  public static Rule alternative(Rule first, Rule second, Rule... rules) {
    return concat(Rule::or, first, second, rules);
  }

  private static Rule concat(BiFunction<Rule, Rule, Rule> bf,
                             Rule r1, Rule r2, Rule... rules) {
    Rule rule = bf.apply(r1, r2);
    for (Rule r : rules) {
      rule = bf.apply(rule, r);
    }
    return rule;
  }
}
