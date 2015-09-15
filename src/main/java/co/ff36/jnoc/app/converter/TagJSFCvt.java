/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package co.ff36.jnoc.app.converter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.Tag;

/**
 * JSF Converter class to map between presentation layer String representations
 * and backing object implementations.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 29, 2013)
 * @author Tarka L'Herpiniere

 */
@Named
@FacesConverter("tagCvt")
public class TagJSFCvt implements Converter {
	private static final Logger LOG = Logger.getLogger(TagJSFCvt.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    /**
     * Normally this would just be a straight database lookup. However because
     * we want to create tags on the fly it is possible to get inputs that have
     * no value so we want to retrieve it from the temporary ID and create a new
     * Tag to be persisted with no ID.
     *
     * @param fc
     * @param uic
     * @param string
     * @return
     */
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    	try {
            List<Tag> tags = dap.findWithNamedQuery("Tag.findByName", QueryParameter.with("name", string).parameters());
            if(tags!=null && tags.size()>0)
            	return tags.get(0);
            return new Tag(string);
        } catch (NumberFormatException e) {
        	LOG.log(Level.CONFIG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
    	try {
            return ((Tag) o).getName();
        } catch (NullPointerException | ClassCastException e) {
        	LOG.log(Level.CONFIG, e.getMessage(), e);
            return null;
        }
    }
//</editor-fold>

}
