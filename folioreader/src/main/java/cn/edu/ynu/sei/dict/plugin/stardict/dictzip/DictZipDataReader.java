/* @(#)DictZipDataReader.java
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package cn.edu.ynu.sei.dict.plugin.stardict.dictzip;

import cn.edu.ynu.sei.dict.plugin.stardict.util.FileAccessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compressed(using dictzip technology compress) dictionary file accessor.
 * <p>
 * More information please refer to doc/StarDictFileFormat file.
 * And, the source original author is Ho Ngoc Duc, please visit his <tt>JDictd Project</tt> 
 * web page: 
 *  <a href="http://www.informatik.uni-leipzig.de/~duc/Java/JDictd/">JDictd's homepage</a>
 * </p>
 * @author Ho Ngoc Duc
 * @author 88250
 * @version 1.1.2.0, Mar 2, 2008
 */
public class DictZipDataReader {

    /**
     * dictionary data file name
     */
    public String dictFileName;

    /**
     * dictionary data file header
     */
    public DictZipHeader zipFileHeader;

    /**
     * Default constructor.
     * @param dictFileName compressed dictionary file name
     */
    public DictZipDataReader(String dictFileName) {
        this.dictFileName = dictFileName;
        initialize();
    }

    void initialize() {
        FileAccessor in = null;
        DictZipInputStream din = null;
        try {
            in = new FileAccessor(dictFileName, "r");
            din = new DictZipInputStream(in);
            zipFileHeader = din.readHeader();
            in.close();
            din.close();
        } catch (IOException e) {
            try {
				throw new FileNotFoundException("Cannot initialize DICTZIP header: " + e);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } finally {
            if (din != null) {
                try {
                    din.close();
                    din = null;
                } catch (IOException ex) {
                    Logger.getLogger(DictZipDataReader.class.getName()).
                            log(Level.SEVERE, null, ex);
                }

            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException ex) {
                    Logger.getLogger(DictZipDataReader.class.getName()).
                            log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    /**
     * Read .dz dictionary data file.
     * @param start data beginning position
     * @param length data size
     * @return all of <code>byte<code> data from beginning position to fill the size
     * @throws java.io.IOException
     */
    public byte[] readData(long start, long length) throws java.io.IOException {
        String s = dictFileName;
        FileAccessor in = null;
        DictZipInputStream din = null;
        try {
            in = new FileAccessor(s, "r");
            din = new DictZipInputStream(in);
            DictZipHeader h = this.zipFileHeader;
            int idx = (int) start / h.chunkLength;
            int off = (int) start % h.chunkLength;
            long pos = h.offsets[idx];
            in.seek(pos);
            byte[] b = new byte[off + (int) length];
            din.readFully(b);
            byte[] ret = new byte[(int) length];
            System.arraycopy(b, off, ret, 0, (int) length);

            return ret;
        } catch (IOException ex) {
            Logger.getLogger(DictZipDataReader.class.getName()).
                    log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                din.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(DictZipDataReader.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            in.close();
            din.close();
        }
    }
}
