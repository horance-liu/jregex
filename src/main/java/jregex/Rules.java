package jregex;

import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;

public final class Rules {
  private Rules() {
  }

  public static final Rule ANY = atom(c -> true);

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

  private static Rule atom(IntPredicate spec) {
    return (str, cont) -> {
      if (str.isEmpty())
        return false;
      else
        return spec.test(str.codePointAt(0))
            && cont.test(str.substring(1));
    };
  }

  public static Rule optional(Rule rule) {
    return (s, cont) -> rule.match(s, cont) || cont.test(s);
  }

  public static Rule many(Rule rule) {
    return (s, cont) -> rule.match(s, rest -> many(rule).match(rest, cont))
                     || cont.test(s);
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

  private static Rule concat(
      BiFunction<Rule, Rule, Rule> bf, Rule init, Rule[] rules) {
    return stream(rules).reduce(init, bf::apply);
  }
}
