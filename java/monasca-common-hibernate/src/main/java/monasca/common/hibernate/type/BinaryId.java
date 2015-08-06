/*
 * Copyright 2015 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package monasca.common.hibernate.type;

import java.io.Serializable;
import java.util.Arrays;

import javax.annotation.Nonnull;

import com.google.common.collect.ComparisonChain;

public class BinaryId
    implements Serializable, Comparable<BinaryId> {
  private static final long serialVersionUID = -4185721060467793903L;
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  private final byte[] bytes;
  private transient String hexBytes = null;

  public BinaryId(final byte[] bytes) {
    this.bytes = bytes;
  }

  private static String bytesToHex(byte[] bytes) {
    final char[] hexChars = new char[bytes.length * 2];
    int j, v;

    for (j = 0; j < bytes.length; j++) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }

    return new String(hexChars);
  }

  public byte[] getBytes() {
    return this.bytes;
  }

  public String toHexString() {
    return this.convertToHex();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof BinaryId)) return false;
    final BinaryId binaryId = (BinaryId) o;
    return Arrays.equals(bytes, binaryId.bytes);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes);
  }

  @Override
  public String toString() {
    return this.convertToHex();
  }

  private String convertToHex() {
    if (this.hexBytes == null) {
      this.hexBytes = bytesToHex(this.bytes);
    }
    return this.hexBytes;
  }

  @Override
  public int compareTo(@Nonnull final BinaryId binaryId) {
    return ComparisonChain
        .start()
        .compare(this.toString(), binaryId.toString())
        .result();
  }
}
