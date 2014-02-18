package com.hpcloud.util;

import java.util.ArrayList;

/**
 * LIFO Stack semantics around an ArrayList for random access support. Indexed access via
 * {@link #get(int)} and iteration via {@link #iterator()} is in reverse order (FIFO). Not
 * threadsafe.
 * 
 * @author Jonathan Halterman
 */
public class Stack<T> extends ArrayList<T> {
  private static final long serialVersionUID = 0L;

  /**
   * Pushes to the top of the stack.
   */
  public void push(T element) {
    add(element);
  }

  /**
   * Pops from the top of the stack.
   */
  public T pop() {
    return remove(size() - 1);
  }

  /**
   * Peeks at the top of the stack.
   */
  public T peek() {
    return get(size() - 1);
  }
}
