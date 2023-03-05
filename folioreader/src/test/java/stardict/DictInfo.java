/*
 * @(#)DictInfo.java
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
package stardict;

/**
 * Dictionary information
 * <p>
 * Every .ifo file must be include the following example: <br>
 * bookname=牛津现代英汉双解词典
 * wordcount=39429     
 * idxfilesize=721264
 * </p>
 * More information please refer to doc/StarDictFileFormat file
 * @author 88250
 * @version 1.1.0.3, Feb 16, 2008
 */
public class DictInfo {

    /**
     * dictionary name
     */
    public String bookName;

    /**
     * dicitonary vocabulary count
     */
    public String wordCount;

    /**
     * vocabulary index file
     */
    public String idxFileSize;

    /**
     * Constructor with arguments
     * @param bookName dictionary name
     * @param wordCount dicitonary vocabulary count
     * @param idxFileSize vocabulary index file
     */
    public DictInfo(String bookName, String wordCount, String idxFileSize) {
        this.bookName = bookName;
        this.wordCount = wordCount;
        this.idxFileSize = idxFileSize;
    }

    @Override
    public String toString() {
        return "Book Name: " + bookName +
                "\nWord Count: " + wordCount +
                "\nIndex File Size: " + idxFileSize;
    }
}
