package com.hpcloud.mon.common.model.alarm;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

class AlarmExpressionErrorListener extends BaseErrorListener {
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
      int charPositionInLine, String msg, RecognitionException e) {
    throw new IllegalArgumentException(String.format("Syntax Error [%d] %s: %s",
        charPositionInLine, msg, offendingSymbol));
  }
}
