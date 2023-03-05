/*
 * @(#)DictIfoReader.java
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
package cn.edu.ynu.sei.dict.plugin.stardict;

import cn.edu.ynu.sei.dict.plugin.stardict.util.FileAccessor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dictionary information reader.
 * <p>
 * More information please refer to doc/StarDictFileFormat file.
 * </p>
 * @author 88250
 * @version 1.1.1.2, Feb 16, 2008
 */
public class DictIfoReader {

    private DictInfo dictInfo;

    private String ifoFileName;

    /**
     * Constructor with argument.
     * @param ifoFileName .ifo file name(full path)
     */
    public DictIfoReader(String ifoFileName) {
        this.ifoFileName = ifoFileName;
    }

    /**
     * Read the dictionary information, and store it to {@link #dictInfo}
     * @return dictionary information
     * @throws java.io.FileNotFoundException 
     */
    public DictInfo readInfo() throws FileNotFoundException {
        FileAccessor reader = null;
        try {
            reader = new FileAccessor(ifoFileName, "r");
            String line;
            String bookName = null;
            String wordCount = null;
            String idxFileSize = null;

            while ((line = reader.readLine()) != null) {
                String[] info = line.split("=");
                if (info[0].equals("bookname")) {
                    info[1] = new String(info[1].getBytes("ISO-8859-1"), "UTF-8");
                    bookName = info[1];
                } else if (info[0].equals("wordcount")) {
                    wordCount = info[1];
                } else if (info[0].equals("idxfilesize")) {
                    idxFileSize = info[1];
                }
            }
            return dictInfo =
                    new DictInfo(bookName, wordCount, idxFileSize);
        } catch (IOException ex) {
            Logger.getLogger(DictIfoReader.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new FileNotFoundException(".ifo file not found, please check it!");
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ex) {
                Logger.getLogger(DictIfoReader.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Get dictionary information description.
     * @return dictionary information
     */
    public DictInfo getDictInfo() {
        return dictInfo;
    }

    /**
     * Get the .ifo file name.
     * @return .ifo file name with full path
     */
    public String getIfoFileName() {
        return ifoFileName;
    }

    /**
     * Set the .ifo file name.
     * @param ifoFileName .ifo file name with full path
     */
    public void setIfoFileName(String ifoFileName) {
        this.ifoFileName = ifoFileName;
    }
}
