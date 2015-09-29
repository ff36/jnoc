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

package co.ff36.jnoc.per.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.castor.util.StringUtil;

import co.ff36.jnoc.per.dap.CrudService;

/**
*
* @version 4.0.0
* @since Build 3.0-SNAPSHOT (Sep 25, 2015)
* @author .x.m.
*/
@NamedQueries({
	 @NamedQuery(name = "Carrier.findAll", query = "SELECT c FROM Carrier c"),
	 @NamedQuery(name = "Carrier.findById", query = "SELECT c FROM Carrier c where c.id = :id"),
})
@Entity
public class Carrier {
	 private static final Logger LOG = Logger.getLogger(Carrier.class.getName());
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
	private int id;
	private String title;
	private String name;
	private String email;
	private String phone;
	private String nocEmail;
	private int btsId;
	private String location;
	private String notes;
	
	@ManyToOne
	@JoinColumn(name="dasId")
	private DAS das;
	
	@Transient
	private List<String> emails;
	@Transient
	private List<String> nocEmails;
	@Transient
	private List<String> phones;
	@Transient
    private CrudService dap;
	
	public Carrier() {
		super();
		init();
	}

	public Carrier(int id) {
		super();
		this.id = id;
		init();
	}

	private void init(){
		this.emails = new ArrayList<String>();
		this.phones = new ArrayList<String>();
		this.nocEmails = new ArrayList<String>();
		
		try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }
		
	}
	
	public void create(){	
		this.buildStrings();
		dap.create(this);
	}

	public void update(){
		this.buildStrings();
		dap.update(this);
	}
	
	public void delete(){
		dap.delete(this.getClass(), this.id);
	}
	
	public List<String> toArray(String str){
		List<String> array = new ArrayList<String>();
		if(str!=null){
			String[] tmps = str.split(",");
			for (String tmp : tmps) {
				array.add(tmp.trim());
			}
		}
		return array;
	}
	
	public String mergeArray(List<String> array){
		String result = "";
		for(int i=0; array!=null && i<array.size(); i++){
			String tmp = array.get(i);
			if(tmp.trim().length()==0){
				continue;
			}
			
			result +=tmp.trim();
			if((i+1)<array.size())
				result += ", ";
		}
		return result;
	}
	
	public void builtArray() {
		this.phones=this.toArray(this.phone);
		this.emails = this.toArray(email);
		this.nocEmails = this.toArray(nocEmail);
	}
	
	public void buildStrings(){
		this.email = this.mergeArray(this.emails);
		this.phone = this.mergeArray(this.phones);
		this.nocEmail = this.mergeArray(this.nocEmails);
	}
	
	public String buildFullCarrier(){
		
		String result = "<table>";
		result += "<tr><td><b>Verizon Point of Contact</b></td><td><b>Verizon Point of Contact</b></td><td><b>Verizon Signal Source Info</b></td></tr>";
		result +="<tr>";
		
		result +="<td>";
		if(this.name!=null){
			result += "Name : "+this.name+"<br/>";
		}
		if(this.title!=null){
			result += "Title : "+this.title+"</br>";
		}
		if(this.phones!=null){
			this.phone = this.mergeArray(this.phones);
			result += "phone : "+this.phone+"</br>";
		}
		if(this.emails!=null){
			this.email = this.mergeArray(this.emails);
			result += "Email : "+this.email;
		}
		result +="</td>";
		
		result += "<td>";
		if(this.nocEmails!=null){
			this.nocEmail = this.mergeArray(this.nocEmails);
			result +="Noc Email : "+this.nocEmail;
		}
		result +="</td>";
		
		result +="<td>";
		if(this.btsId>0)
			result += "Bts ID : "+this.btsId+"</br>";
		if(this.location!=null){
			result += "Location : "+this.location+"</br>";
		}
		result +="</td>";
		result +="</tr>";
		
		if(this.notes!=null){
			result +="<tr colspan='3'><td><b>Notes</b><br/>"+this.notes+"</td></tr>";
		}
		result += "</table>";
		return result;
	}
	
	public List<String> completeInfo(String query){
		List<String> results = new ArrayList<String>();
		results.add(query);
		return results;
	}
	
	//==============getter & setter===========================
	
	public int getId() {
		return id;
	}

	public DAS getDas() {
		return das;
	}

	public void setDas(DAS das) {
		this.das = das;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
//		if(StringUtils.isBlank(this.email) || !this.emails.isEmpty())
//			this.email = this.mergeArray(this.emails);
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
//		if(StringUtils.isBlank(this.phone) || !this.phones.isEmpty())
//			this.phone = this.mergeArray(this.phones);
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNocEmail() {
		if(StringUtils.isBlank(this.nocEmail) || !this.nocEmails.isEmpty())
			this.nocEmail = this.mergeArray(this.nocEmails);
		return nocEmail;
	}

	public void setNocEmail(String nocEmail) {
		this.nocEmail = nocEmail;
	}

	public int getBtsId() {
		return btsId;
	}

	public void setBtsId(int btsId) {
		this.btsId = btsId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public List<String> getNocEmails() {
		return nocEmails;
	}

	public void setNocEmails(List<String> nocEmails) {
		this.nocEmails = nocEmails;
	}

	public List<String> getPhones() {
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Carrier [title=" + title + ", name=" + name + ", email="
				+ email + ", phone=" + phone + ", nocEmail=" + nocEmail
				+ ", btsId=" + btsId + ", location=" + location + ", notes="
				+ notes + ", das=" + das + ", emails=" + emails
				+ ", nocEmails=" + nocEmails + ", phones=" + phones + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Carrier other = (Carrier) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
