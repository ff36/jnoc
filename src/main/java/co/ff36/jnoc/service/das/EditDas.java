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

package co.ff36.jnoc.service.das;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import co.ff36.jnoc.app.misc.IpAddress;
import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Carrier;
import co.ff36.jnoc.per.entity.Contact;
import co.ff36.jnoc.per.entity.DAS;
import co.ff36.jnoc.service.navigation.Navigator;

/**
 *
 * @version 4.0.0
 * @since Build 3.0-SNAPSHOT (Sep 25, 2015)
 * @author .x.m.
 */
@Named
@ViewScoped
public class EditDas implements Serializable{

	private static final long serialVersionUID = 6513953652643217972L;

	private boolean render;
	private DAS das;
	private String dasID;
	
	
	@EJB
	private CrudService dap;
	@Inject
	private Navigator navigator;
	
	public EditDas(){
		this.render = true;
		this.das = new DAS();
		this.dasID = JsfUtil.getRequestParameter("das");
		
		try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
        }
		
		this.init();
	}

	public void init(){
		try {
            // Get the das from the persistence layer
			das = (DAS) dap.find(DAS.class, Long.valueOf(dasID));
			
			IpAddress ips = new IpAddress();
			if(das.getDmsIp()!=null){
				String[] temps = das.getDmsIp().split("\\.");
				ips.setIpa(temps[0]);
				ips.setIpb(temps[1]);
				ips.setIpc(temps[2]);
				ips.setIpd(temps[3]);
			}
			das.setIpAddress(ips);
			
			if(this.das.getContact()==null)
				this.das.setContact(new Contact());

			if(this.das.getCarriers()==null){
				this.das.setCarriers(new HashSet<Carrier>());
			}else{
				for (Carrier carrier : this.getDas().getCarriers()) {
					carrier.builtArray();
				}
			}
			
        } catch (NullPointerException | NumberFormatException e) {
            // The das was not found in the persistence
            navigator.navigate("LIST_DAS");
        }
        render = true;
	}
	
	public boolean isRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public DAS getDas() {
		return das;
	}

	public void setDas(DAS das) {
		this.das = das;
	}

	public Navigator getNavigator() {
		return navigator;
	}

}
