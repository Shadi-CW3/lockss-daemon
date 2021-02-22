/*

Copyright (c) 2000-2021, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.devtools.marc;

import java.io.*;

import org.marc4j.*;

/**
 * <p>
 * Converts from binary MARC to MARC XML.
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.76
 */
public class MarcToMarcXml {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: MarcToMarcXml INFILE.mrc OUTFILE.xml");
      System.exit(1);
    }
    String inFileStr = args[0];
    String outFileStr = args[1];
    try (InputStream inStream = new FileInputStream(inFileStr) ;
         OutputStream outStream = new FileOutputStream(outFileStr)) {
      MarcXmlWriter writer = null;
      try {
        MarcStreamReader reader = new MarcStreamReader(inStream);
        writer = new MarcXmlWriter(outStream, true);
        while (reader.hasNext()) {
          writer.write(reader.next());
        } 
      }
      finally {
        if (writer != null) {
          writer.close();
        }
      }
    }
  }
  
}
