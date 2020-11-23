# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.

import gzip
from io import BytesIO
import struct

_XERIAL_V1_HEADER = (-126, b'S', b'N', b'A', b'P', b'P', b'Y', 0, 1, 1)
_XERIAL_V1_FORMAT = 'bccccccBii'

try:
    import snappy
    _HAS_SNAPPY = True
except ImportError:
    _HAS_SNAPPY = False


def has_gzip():
    return True


def has_snappy():
    return _HAS_SNAPPY


def gzip_encode(payload, compresslevel=None):
    if not compresslevel:
        compresslevel = 9

    with BytesIO() as buf:

        # Gzip context manager introduced in python 2.6
        # so old-fashioned way until we decide to not support 2.6
        gzipper = gzip.GzipFile(fileobj=buf, mode="w", compresslevel=compresslevel)
        try:
            gzipper.write(payload)
        finally:
            gzipper.close()

        result = buf.getvalue()

    return result


def gzip_decode(payload):
    with BytesIO(payload) as buf:

        # Gzip context manager introduced in python 2.6
        # so old-fashioned way until we decide to not support 2.6
        gzipper = gzip.GzipFile(fileobj=buf, mode='r')
        try:
            result = gzipper.read()
        finally:
            gzipper.close()

    return result


def snappy_encode(payload, xerial_compatible=False, xerial_blocksize=32 * 1024):
    """Encodes the given data with snappy if xerial_compatible is set then the
       stream is encoded in a fashion compatible with the xerial snappy library

       The block size (xerial_blocksize) controls how frequent the blocking
       occurs 32k is the default in the xerial library.

       The format winds up being
        +-------------+------------+--------------+------------+--------------+
        |   Header    | Block1 len | Block1 data  | Blockn len | Blockn data  |
        |-------------+------------+--------------+------------+--------------|
        |  16 bytes   |  BE int32  | snappy bytes |  BE int32  | snappy bytes |
        +-------------+------------+--------------+------------+--------------+

        It is important to not that the blocksize is the amount of uncompressed
        data presented to snappy at each block, whereas the blocklen is the
        number of bytes that will be present in the stream, that is the
        length will always be <= blocksize.
    """

    if not has_snappy():
        raise NotImplementedError("Snappy codec is not available")

    if xerial_compatible:
        def _chunker():
            for i in range(0, len(payload), xerial_blocksize):
                yield payload[i:i + xerial_blocksize]

        out = BytesIO()

        header = b''.join([struct.pack('!' + fmt, dat) for fmt, dat
                           in zip(_XERIAL_V1_FORMAT, _XERIAL_V1_HEADER)])

        out.write(header)
        for chunk in _chunker():
            block = snappy.compress(chunk)
            block_size = len(block)
            out.write(struct.pack('!i', block_size))
            out.write(block)

        out.seek(0)
        return out.read()

    else:
        return snappy.compress(payload)


def _detect_xerial_stream(payload):
    """Detects if the data given might have been encoded with the blocking mode
        of the xerial snappy library.

        This mode writes a magic header of the format:
            +--------+--------------+------------+---------+--------+
            | Marker | Magic String | Null / Pad | Version | Compat |
            |--------+--------------+------------+---------+--------|
            |  byte  |   c-string   |    byte    |  int32  | int32  |
            |--------+--------------+------------+---------+--------|
            |  -126  |   'SNAPPY'   |     \0     |         |        |
            +--------+--------------+------------+---------+--------+

        The pad appears to be to ensure that SNAPPY is a valid cstring
        The version is the version of this format as written by xerial,
        in the wild this is currently 1 as such we only support v1.

        Compat is there to claim the miniumum supported version that
        can read a xerial block stream, presently in the wild this is
        1.
    """

    if len(payload) > 16:
        header = struct.unpack('!' + _XERIAL_V1_FORMAT, bytes(payload)[:16])
        return header == _XERIAL_V1_HEADER
    return False


def snappy_decode(payload):
    if not has_snappy():
        raise NotImplementedError("Snappy codec is not available")

    if _detect_xerial_stream(payload):
        # TODO ? Should become a fileobj ?
        out = BytesIO()
        byt = payload[16:]
        length = len(byt)
        cursor = 0

        while cursor < length:
            block_size = struct.unpack_from('!i', byt[cursor:])[0]
            # Skip the block size
            cursor += 4
            end = cursor + block_size
            out.write(snappy.decompress(byt[cursor:end]))
            cursor = end

        out.seek(0)
        return out.read()
    else:
        return snappy.decompress(payload)
