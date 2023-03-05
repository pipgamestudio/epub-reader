/*
 * @(#)StarDictActivator.java
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

import cn.edu.ynu.sei.dict.plugin.dictsmanager.service.IDictsManager;
import cn.edu.ynu.sei.dict.service.core.IDictQueryService;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * StarDict query engine plugin activator.
 * @author 88250
 * @version 1.0.1.0, Mar 8, 2008
 */
public class StarDictActivator implements BundleActivator {

    private IDictQueryService stardictReader;
    private IDictsManager dictsManager;

    public void start(BundleContext bundleContext) throws Exception {
        ServiceReference sr = bundleContext.getServiceReference(IDictsManager.class.getName());
        dictsManager = (IDictsManager) bundleContext.getService(sr);
        String dictsPath = dictsManager.getDictsPath();
        String dictFileDir = dictsPath + "/stardict-langdao-ec-gb-2.4.2";
        String dictName = "langdao-ec-gb";

        stardictReader = new StarDictReader(dictFileDir,
                dictName);

        bundleContext.registerService(IDictQueryService.class.getName(),
                stardictReader,
                new Hashtable());

    }

    public void stop(BundleContext bundleContext) throws Exception {
        stardictReader = null;
    }
}
