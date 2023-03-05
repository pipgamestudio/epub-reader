/*
 * @(#)DictEngine.java
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

import cn.edu.ynu.sei.dict.service.core.IDictQueryService;
import cn.edu.ynu.sei.dict.service.core.Word;
import cn.edu.ynu.sei.dict.service.exception.NotFoundWordException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StarDict dictionary reader.
 * <p>
 * The <code>StarDictReader</code> contains three parts as the essentials:
 * <ol>
 * <li>DictIfoReader</li>
 * <li>DictIndexReader</li>
 * <li>DictDataReader</li>
 * </ol>
 * Please refer to their documents for details.
 * </p>
 * @author 88250
 * @author zy
 * @version 1.1.5.5, Mar 7, 2008
 * @see cn.edu.ynu.sei.dict.plugin.stardict.DictIfoReader
 * @see cn.edu.ynu.sei.dict.plugin.stardict.DictIndexReader
 * @see cn.edu.ynu.sei.dict.plugin.stardict.DictDataReader
 */
public class StarDictReader implements IDictQueryService {

    /**
     * dictionary information file reader
     */
    private DictIfoReader ifoReader;

    /**
     * dictionary index entry file reader
     */
    private DictIndexReader idxReader;

    /**
     * dictionary sparse index entry file reader
     */
    // TODO private DictIdxCache idxCacheReader;
    /**
     * dictionary data file reader
     */
    private DictDataReader dataReader;

    /**
     * Constructor with arguments. 
     * @param dictFileDir dictionary files directory
     * @param dictName dictionary name, without file suffix
     * @throws java.io.FileNotFoundException 
     */
    public StarDictReader(String dictFileDir, String dictName)
            throws FileNotFoundException {
        String dictPath = dictFileDir + File.separator + dictName;
        ifoReader = new DictIfoReader(dictPath + ".ifo");
        idxReader = new DictIndexReader(dictPath + ".idx");
        // idxCacheReader = new DictIdxCache(dictPath + ".idx");
        dataReader = new DictDataReader(dictPath + ".dict.dz");
        if (ifoReader != null) ifoReader.readInfo();
        if (idxReader != null) idxReader.readIndexFile();
    }

    @Override
    public String lookupList(String word) throws NotFoundWordException {
        String result = "";

        int j = 0;
        int i = 0;
        for (; i < idxReader.getDictIndexList().size(); i++) {
            String wordTemp = idxReader.getDictIndexList().get(i).word;
            if (wordTemp.length() >= word.length()) {
                if (wordTemp.substring(0, word.length()).toLowerCase().equals(word.toLowerCase())) {
                    j = i;
                    break;
                }
            }
        }

        for (; j < idxReader.getDictIndexList().size() && j < i + 30; j++) {
            String wordTemp = idxReader.getDictIndexList().get(j).word;
            result += wordTemp + "-";
        }
        result += "@";

        if (result.equals("@")) {
            throw new NotFoundWordException();
        }
        return result;
    }

    @Override
    public Word lookup(String word) throws NotFoundWordException {
        int pos = binarySearch(idxReader.getDictIndexList(), word);

        if (pos < 0) {
            throw new NotFoundWordException(word);
        }
        DictIndex index =
                idxReader.getDictIndexList().get(pos);

        try {
            String wordDefinition =
                    dataReader.readData(index.offset, index.size);
            Word ret = new Word();
            ret.self = word;
            ret.definition = wordDefinition;
            ret.defComeFrom = ifoReader.getDictInfo().bookName;

            return ret;
        } catch (IOException ex) {
            Logger.getLogger(StarDictReader.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new NotFoundWordException(ex.getMessage());
        }
    }

    /**
     * Search all vocabularies in dictionary data file.
     * <p>
     * <b>Notice: </b>This method maybe cause <tt>Java heap space overflow</tt>.
     * </p>
     * @param size  the word list size of wanna
     * @return a <code>List</code> fill with <code>Word</code>
     * @throws cn.edu.ynu.sei.dict.service.exception.NotFoundWordException 
     */
    public List<Word> fineAll(int size) throws NotFoundWordException {
        List<Word> ret = new ArrayList<Word>();
        int totalSize = idxReader.getDictIndexList().size();

        size = size >= totalSize ? totalSize : size;
        for (int j = 0; j < size; j++) {
            Word word = new Word();
            word = lookup(idxReader.getDictIndexList().get(j).word);
            ret.add(word);
        }

        return ret;
    }

    /**
     * Ordinary binary search method for <code>dictIndexList</code>
     * @param dictIndexList the dictionary word list to be searched
     * @param word key
     * @return key index
     * @see java.util.Arrays#binarySearch
     */
    private int binarySearch(List<DictIndex> dictIndexList, String word) {
        int low = 0;
        int high = dictIndexList.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            DictIndex midVal = dictIndexList.get(mid);
            if (midVal.word.compareTo(word) < 0) {
                low = mid + 1;
            } else if (midVal.word.compareTo(word) > 0) {
                high = mid - 1;
            } else {
                return mid; // key found

            }
        }

        return -(low + 1);  // key not found

    }

    /**
     * Get the dictionary index file reader.
     * @return dictionary index file reader
     */
    public DictIndexReader getIdxReader() {
        return idxReader;
    }
}
