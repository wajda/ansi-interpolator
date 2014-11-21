package org.backuity.ansi

import org.backuity.ansi.AnsiFormatter.AnsiContext
import org.backuity.matchete.JunitMatchers
import org.junit.Test

class AnsiFormatterTest extends JunitMatchers {

  import AnsiFormatter.ansiPart
  import org.backuity.ansi.{AnsiCodes => Ansi}

  val ctx = new AnsiContext

  @Test
  def nestedTags(): Unit = {
    ansiPart("%bold{hey %underline{this} is %yellow{the shit}}", ctx) must_==
      Ansi.BOLD + "hey " + Ansi.UNDERLINE + "this" + Ansi.UNDERLINE_OFF + " is " + Console.YELLOW + "the shit" + Ansi.COLOR_DEFAULT + Ansi.BOLD_OFF
  }

  @Test
  def unclosedTagMustStackClosingTagOnTheContext(): Unit = {
    ansiPart("%bold{hey %underline{you}", ctx) must_== Ansi.BOLD + "hey " + Ansi.UNDERLINE + "you" + Ansi.UNDERLINE_OFF
    ctx.pop() must_== Ansi.BOLD_OFF
  }

  @Test
  def singlePercentMustBeLeftVerbatim(): Unit = {
    ansiPart("a trailing %", ctx) must_== "a trailing %"
    ansiPart("a % alone", ctx) must_== "a % alone"
  }

  import AnsiFormatter.ParsingError

  @Test
  def parsingErrorMustReportErrorOffset(): Unit = {
    ansiPart("an erroneous %xxx{blabla}", ctx) must throwA[ParsingError].like("with correct offset") {
      case ParsingError(msg, offset) =>
        offset must_== "an erroneous %".length
        msg must_== "Unsupported tag xxx"
    }
  }
}
