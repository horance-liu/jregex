package jregex

import spock.lang.Specification

import static jregex.Rules.*

class RuleSpec extends Specification {
  private void matched(Rule rule, String str) {
    rule.match(str) == true;
  }

  private void mismatched(Rule rule, String str) {
    rule.match(str) == false;
  }

  def "empty string"() {
    expect:
    matched(val(""), "")
    mismatched(val(""), "a")
  }

  def "one character"() {
    expect:
    matched(val('c'), "c")
    mismatched(val('c'), "b")
    mismatched(val('c'), "")
  }


  def "one character: escaped"() {
    expect:
    matched(val('\n'), "\n")
    mismatched(val('\n'), "\t")
    mismatched(val('\n'), "")
  }

  def "any character"() {
    expect:
    matched(ANY, "a")
  }

  def "simple string"() {
    expect:
    matched(val("abc"), "abc")
    mismatched(val("abc"), "")
    mismatched(val("abc"), "bcd")
  }

  def "sequence: char + char"() {
    expect:
    matched(val('a') + val('b'), "ab")
    mismatched(val('b') + val('a'), "ab")
  }

  def "multi sequence: char + char + char"() {
    expect:
    matched(val('a') + val('b') + val('c'), "abc")
    mismatched(val('c') + val('b') + val('a'), "abc")
  }

  def "sequence: string + string"() {
    expect:
    matched(val("abc") + val("bcd"), "abcbcd")
    mismatched(val("bcd") + val("abc"), "abcbcd")
  }

  def "multi sequence: string + string + string"() {
    expect:
    matched(val("abc") + val("bcd") +val("xyz"), "abcbcdxyz")
    mismatched(val("xyz") + val("bcd") + val("abc"), "abcbcdxyz")
  }

  def "sequence: string + char"() {
    expect:
    matched(val("abc") + val("d"), "abcd")
    matched(val('d') + val("abc"), "dabc")
  }

  def "alternative: char | char"() {
    expect:
    def rule = val('c') | val('d')

    matched(rule, "c")
    matched(rule, "d")
  }

  def "multi alternative: char | char | char"() {
    expect:
    def rule = val('c') | val('d') | val('e')

    matched(rule, "c")
    matched(rule, "d")
    matched(rule, "e")
    mismatched(rule, "f")
  }

  def "alternative: string | string"() {
    expect:
    def rule = val("abc") | val("bcd")

    matched(rule, "abc")
    matched(rule, "bcd")
  }

  def "multi alternative: string | string | string"() {
    expect:
    def rule = val("abc") | val("bcd") | val("xyz")

    matched(rule, "abc")
    matched(rule, "bcd")
    matched(rule, "xyz")
  }

  def "alternative: string | char"() {
    expect:
    def rule = val("abc") | val('\n')

    matched(rule, "abc")
    matched(rule, "\n")
  }

  def "zero or more(greedy, non-backoff): a*b"() {
    expect:
    def rule = many(val('a')) + val('b')

    matched(rule, "b")
    matched(rule, "ab")
    matched(rule, "aab")
    mismatched(rule, "c")
    mismatched(rule, "ac")
  }

  def "one or more(greedy, non-backoff): b+cde"() {
    expect:
    def rule = oneOrMore(val('b')) + val("cde")

    mismatched(rule, "cde")
    matched(rule, "bcde")
    matched(rule, "bbcde")
    matched(rule, "bbcdef")
  }

  def "one or more(greedy, backoff): (b|d)+bbc"() {
    expect:
    def rule = (oneOrMore(val('b') | val('d'))) + val("bbc")

    matched(rule, "bbbc")
    matched(rule, "dbbc")
  }

  def "oneof('abc'): [bd]+bbc"() {
    expect:
    def rule = oneOrMore(oneOf("bd")) + val("bbc")

    matched(rule, "bbbc")
    matched(rule, "dbbc")
  }

  def "one or more(greedy, non-backoff): b+cde\$"() {
    expect:
    def rule = eof(oneOrMore(val('b')) + val("cde"))

    mismatched(rule, "cde")
    matched(rule, "bcde")
    matched(rule, "bbcde")
    mismatched(rule, "bbcdef")
  }

  def "optional(greedy, non-backoff): b?cde"() {
    expect:
    def rule = optional(val('b')) + val("cde")

    matched(rule, "cde")
    matched(rule, "bcde")
    matched(rule, "bcdef")
  }
}
