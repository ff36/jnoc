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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import co.ff36.jnoc.per.dap.CrudService;
/**
 * @version 4.0.0
 * @since Build 2.0-SNAPSHOT (Oct 10, 2015)
 * @author xm
 */
@NamedQueries({
    @NamedQuery(name = "Role.findAll", query = "SELECT r FROM role r"),
    @NamedQuery(name = "Role.findByID", query = "SELECT r FROM role r WHERE r.id = :id")})
@Entity(name="role")
public class Role implements java.io.Serializable{
	private static final Logger LOG = Logger.getLogger(Role.class.getName());
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
	private Long id;
	
	private String name;
	
	@ManyToMany(mappedBy="roles")
	private List<User> users = new ArrayList<User>();
	
	@ManyToMany
	@JoinTable(
		name="role_permission_template", 
		joinColumns={@JoinColumn(name="Role_ID")},
		inverseJoinColumns={@JoinColumn(name="Permission_template_ID")}
	)
	private List<PermissionTemplate> permissions = new ArrayList<PermissionTemplate>();
	
	@Transient
    private CrudService dap;	
	@Transient
	private List<User> allUsers = new ArrayList<User>();
	@Transient
	private List<PermissionTemplate> AllPermissions = new ArrayList<PermissionTemplate>();
	
	public Role() {
		super();
		try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }
	}

	public String showRole(){
		String temp = this.name + " : ";
		for (int i = 0; i < this.getPermissions().size(); i++) {
			temp += this.getPermissions().get(i).getExpression();
			if(this.permissions.size()>(i+1)){
				temp+=",";
			}
		}
		return temp;
	}
	
	///////////////////getter -- setter////////////////////////

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<PermissionTemplate> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionTemplate> permissions) {
		this.permissions = permissions;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public List<PermissionTemplate> getAllPermissions() {
		return AllPermissions;
	}

	public void setAllPermissions(List<PermissionTemplate> allPermissions) {
		AllPermissions = allPermissions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
