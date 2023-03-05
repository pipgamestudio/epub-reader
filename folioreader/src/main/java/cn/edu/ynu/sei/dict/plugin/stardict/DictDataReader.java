/*
 * @(#)DictDataReader.java
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
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
package cn.edu.ynu.sei.dict.plugin.stardict;

import cn.edu.ynu.sei.dict.plugin.stardict.dictzip.DictZipDataReader;
import java.io.IOException;

/**
 * Dictionary word data(including word definition or translation) reader.
 * <p>
 * More information please refer to doc/StarDictFileFormat file.
 * </p>
 * @author 88250
 * @version 1.1.1.7, Mar 2, 2008
 */
public class DictDataReader {

    DictZipDataReader da;

    /**
     * Constructor with argument
     * @param dataFileName .dict.dz file name
     */
    public DictDataReader(String dataFileName) {
        da = new DictZipDataReader(dataFileName);
    }

    /**
     * Read .dz dictionary data file.
     * @param start data beginning position
     * @param length data size
     * @return <code>String</code> type data 
     * @throws java.io.IOException
     */
    public String readData(long start, long length) throws IOException {
        byte[] bytes = da.readData(start, length);
        return new String(bytes, "UTF-8");
    }
}
