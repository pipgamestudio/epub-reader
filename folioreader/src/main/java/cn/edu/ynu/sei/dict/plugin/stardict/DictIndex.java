/*
 * @(#)DictIndex.java
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

/**
 * Dictionary index entry structure. 
 * <p>
 * Each index entry in the word list contains three fields, one after the other:
 *   word_str;          // a utf-8 string terminated by '\0'.
 *   word_data_offset;  // word data's offset in .dict file
 *   word_data_size;    // word data's total size in .dict file
 * </p>
 * More information please refer to doc/StarDictFileFormat file
 * @author 88250
 * @version 1.1.0.2, Feb 16, 2008
 */
public class DictIndex {

    /**
     * word string
     */
    public String word;

    /**
     * word data offset
     */
    public long offset;

    /**
     * word data size
     */
    public long size;

    @Override
    public String toString() {
        return word + "\t" + offset + "\t" + size;
    }
}
