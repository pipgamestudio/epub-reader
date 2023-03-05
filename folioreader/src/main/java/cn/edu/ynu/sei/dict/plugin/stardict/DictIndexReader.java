/*
 * @(#)DictIndexReader.java
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

import cn.edu.ynu.sei.dict.plugin.stardict.util.ByteConverter;
import cn.edu.ynu.sei.dict.plugin.stardict.util.FileAccessor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dictionary word list index entry structrue reader.
 * <p>
 * More information please refer to doc/StarDictFileFormat file.
 * </p>
 * @author 88250
 * @version 1.2.1.3, Mar 2, 2008
 */
public class DictIndexReader {

    /**
     * the word index entry list
     */
    private List<DictIndex> dictIndexList =
            new ArrayList<DictIndex>();

    private String indexFileName;

    /**
     * Constructor with argument.
     * @param indexFileName .idx file name(full path).
     */
    public DictIndexReader(String indexFileName) {
        this.indexFileName = indexFileName;
    }

    /**
     * Read the dictionary index file, store the index entries 
     * into {@link #dictIndexList}
     * @throws java.io.FileNotFoundException 
     * @see cn.edu.ynu.sei.dict.kernel.core.fixed.reader.stardict.DictIndex
     */
    public void readIndexFile() throws FileNotFoundException {
        FileAccessor reader = null;
        
        try {
            reader = new FileAccessor(indexFileName, "r");
            // the maximun length of a word  must less 256
            // 256 bytes(word) + 1 byte('\0') + 4 bytes(offset) + 4 bytes(def size)
            byte[] bytes = new byte[256 + 1 + 4 + 4];
            int currentPos = 0;
            while (reader.read(bytes, 0, bytes.length) != -1) {
                int j = 0;
                boolean isWordPart = true;
                boolean isOffsetPart = false;
                boolean isSizePart = false;
                String word = null;
                long offset = 0;        // offset of a word in data file
                long size = 0;          // size of word's defition
                int wordLength = 0;     // the byte(s) length of a word

                for (int i = 0; i < bytes.length; i++) {
                    if (isWordPart) {
                        if (bytes[i] == 0) {
                            wordLength = i;
                            word = new String(bytes, j, i - j, "UTF-8");
                            j = i;
                            isWordPart = false;
                            isOffsetPart = true;
                        }
                        continue;
                    }
                    if (isOffsetPart) {
                        i += 3;
                        j++; // skip the split token: '\0'
                        if (i >= bytes.length) {
                            i = bytes.length - 1;
                        }
                        offset = ByteConverter.unsigned4BytesToInt(bytes, j);
                        j = i + 1;
                        isOffsetPart = false;
                        isSizePart = true;
                        continue;
                    }
                    if (isSizePart) {
                        i += 3;
                        if (i >= bytes.length) {
                            i = bytes.length - 1;
                        }
                        size = ByteConverter.unsigned4BytesToInt(bytes, j);
                        j = i + 1;
                        isSizePart = false;
                        isWordPart = true;
                    }

                    DictIndex dictIndex = new DictIndex();
                    dictIndex.word = word;
                    dictIndex.offset = offset;
                    dictIndex.size = size;
                    dictIndexList.add(dictIndex);

                    // skip current index entry
                    int indexSize = wordLength + 1 + 4 + 4;
                    reader.seek(indexSize + currentPos);
                    currentPos += indexSize;
                    break;
                }
            }
            reader.close();

            sortIndexList(dictIndexList);
        } catch (IOException ex) {
            Logger.getLogger(DictIndexReader.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(DictIndexReader.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Get word index entry list.
     * @return word index entry list
     */
    public List<DictIndex> getDictIndexList() {
        return dictIndexList;
    }

    /**
     * Set the .idx file name.
     * @return .idx file name with full path
     */
    public String getIndexFileName() {
        return indexFileName;
    }

    /**
     * Set the .idx file name.
     * @param indexFileName .idx file name with full path
     */
    public void setIndexFileName(String indexFileName) {
        this.indexFileName = indexFileName;
    }

    @SuppressWarnings("unchecked")
    private void sortIndexList(List<DictIndex> dictIndexList) {
        java.util.Collections.sort(dictIndexList,
                                   new Comparator() {

                                       public int compare(Object o1,
                                                           Object o2) {
                                           DictIndex m1 =
                                                   (DictIndex) o1;
                                           DictIndex m2 =
                                                   (DictIndex) o2;
                                           return m1.word.compareTo(m2.word);
                                       }
                                   });

    }
}
