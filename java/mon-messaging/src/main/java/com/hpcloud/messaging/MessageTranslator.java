package com.hpcloud.messaging;

import net.jodah.typetools.TypeResolver;
import net.jodah.typetools.TypeResolver.Unknown;

import com.google.common.base.Preconditions;

/**
 * Translates a message, breaking the dependency between separate application's data formats.
 * 
 * @param <I> input type
 * @param <O> output type
 * @see <a href="http://www.eaipatterns.com/MessageTranslator.html">Message Translator</a>
 * @author Jonathan Halterman
 */
public abstract class MessageTranslator<I, O> {
  protected final Class<I> inputType;
  protected final Class<O> outputType;

  protected MessageTranslator(Class<I> inputType, Class<O> outputType) {
    this.inputType = inputType;
    this.outputType = outputType;
  }

  @SuppressWarnings("unchecked")
  public MessageTranslator() {
    Class<?>[] typeArguments = TypeResolver.resolveRawArguments(MessageTranslator.class, getClass());
    Preconditions.checkArgument(typeArguments[0] != Unknown.class
        && typeArguments[1] != Unknown.class,
        "Must declare input type argument <I> and output type argument <O> for Translator");
    inputType = (Class<I>) typeArguments[0];
    outputType = (Class<O>) typeArguments[1];
  }

  /** Returns the translator's input type, else {@link Unknown} if one cannot be resolved. */
  public Class<I> inputType() {
    return inputType;
  }

  /** Returns the translator's output type, else {@link Unknown} if one cannot be resolved. */
  public Class<O> outputType() {
    return outputType;
  }

  /** Translates the {@code messageBody} to an output format. */
  public abstract O translate(I messageBody);
}
