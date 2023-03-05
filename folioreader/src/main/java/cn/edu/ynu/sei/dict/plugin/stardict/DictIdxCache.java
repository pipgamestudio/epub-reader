/*
 * @(#)DictIdxCache.java
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dictionary index entry file cache manager.
 * <p>
 * If some .idx file's size is huge, for instance, some.idx file's size is
 * more then 10MB. At this point, we must create a sparse(or secondary) index
 * file, which more sparser then the original word index file.
 * </p>
 * @author 88250
 * @version 1.0.1.0, Mar 5, 2008
 */
public class DictIdxCache {

    private List<DictIndex> dictSprIndexList = new ArrayList<DictIndex>();

    private DictIndexReader idxReader;

    private final int SPARSE_VALUE = 32;

    /**
     * Constructor with arguments.
     * <p>
     * If sparse index file existed, read it into memory, otherwise, 
     * create a sparse index for <code>orginialIdxFileName</code>.
     * </p>
     * <b>Notice:</b> the sparse index file name is 
     * <code>orginalIdxFileName</code> append a suffix "spr".
     * i.e. <tt>someDict.idx.spr</tt>; and all filename refer to are
     * <b>full path</b>.
     * @param originalIdxFileName original index file name
     */
    public DictIdxCache(String originalIdxFileName) {
        String sprFileName = originalIdxFileName;

        if (inexistSparseIndexFile(sprFileName)) {
            createSparseIndex(originalIdxFileName);
        } else {
            readSparseIndex(sprFileName);
        }

    }

    /**
     * Create a sparse index file using pure text format.
     * @param originalIdxFilePath
     */
    void createSparseIndex(String originalIdxFilePath) {

        System.out.println(originalIdxFilePath);
        idxReader = new DictIndexReader(originalIdxFilePath);

        try {
            idxReader.readIndexFile();
            List<DictIndex> idxList = idxReader.getDictIndexList();
            File sprFile = new File(originalIdxFilePath + ".spr");
            sprFile.createNewFile();
            FileAccessor fa = new FileAccessor(originalIdxFilePath, "rw");

            for (int i = 0; i < idxList.size(); i += SPARSE_VALUE) {
                DictIndex index = idxList.get(i);
                dictSprIndexList.add(index);
                fa.writeUTF(index.toString());
            }
            fa.close();
        } catch (IOException ex) {
            Logger.getLogger(DictIdxCache.class.getName()).
                    log(Level.SEVERE, null,
                        ex);
        }
    }

    /**
     * Read the sparse index file, load all sparse index entries into memory.
     * @param sparseIdxFilePath sparse index file name with full path
     */
    void readSparseIndex(String sparseIdxFilePath) {
        FileAccessor reader = null;
        try {
            reader = new FileAccessor(sparseIdxFilePath, "r");
            byte[] bytes = new byte[1024 * 8];
            reader.read(bytes);
            for (int i = 0; i <
                    bytes.length; i++) {
                byte b = bytes[i];
            }

            System.out.println(new String(bytes));

        } catch (IOException ex) {
            Logger.getLogger(DictIdxCache.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(DictIdxCache.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

    }

    private boolean inexistSparseIndexFile(String sprFileName) {
        File sprFile = new File(sprFileName);

        return !sprFile.isFile();
    }
}
