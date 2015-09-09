# JNOC
**Lead Developer:** Tarka L'Herpiniere

#### License
Copyright (C) 2015  555 inc ltd.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 

#### Technology Stack
**Java EE** (Version 8)
**Maven** (Version 3)
**Glassfish** (Version 4)
**Mojarra** (Version 2.2)

---

#### External Dependencies
**Amazon Web Services**

---

#### Versioning
**Git** (Github)

---

### Build Information
*TODO*

---

### Deployment Information
*TODO*

---

### Versioning Information
The application uses [**Semantic Versioning**](http://semver.org/) to version its releases. The following is an extract explanation of the versioning system used;

---

### Permissions
Permissions are granted to user accounts to allow them to access and perform services. Permissions work by defensively **granting** access to a restricted services, this means that by default an account has the minimum level of permissions. Additional permissions need to be granted in order to allow that user to access and manipulate the desired services.

Permissions are granted in the following format;

```
SERVICES:FUNCTIONS
```

Both services and functions can be specified as comma separate lists with no spaces;

```
SERVICE,SERVICE,SERVICE:FUNCTION,FUNCTION
```

All services supplied on the left of the colon (`:`) will be granted the function on the right of the colon. So the following would grant a user permission to view (access) and create both sites and accounts;

```
account,site:access,create
```

To specify different levels of granularity within a users permissions multiple permissions can be granted. The following would grant 'access' and 'create' functions to 'account' services, but only 'access' to 'sites';

```
account:access,create
site:access
```

>The `*` can be specified as a wildcard for both services and functions.

Permissions are bound by the users account type (metier). Granting a VAR account wildcard permissions does not mean that they have the same access rights as an administrator granted the same wildcard permissions. It simply implies that within the VAR user account they have all the functions available in all the services that are available as a VAR.

| Services       | Description        |
|-------------- |------------------ |
| `account`      | User accounts.     |
| `ticket`       | Support tickets.    |
| `incident`     | Incident tickets.  |
| `company`      | VAR and Client companies. |
| `das`          | DAS installation sites. |
| `report`       | DAS reporting. |
| `knowledge`    | Egnyte file storage. |
| `rma`          | Return material authorization. |
| `permission`   | User permissions. By default the 'account' Service allows all account features to be manipulated **EXCEPT** permissions. |
| `*`            | All of the above (Use sparingly) |


| Functions     | Description        |
|-------------- |------------------ |
| `access`        | Grants privilege to 'access' the specified 'Service'. 'create, edit, delete' functions must be accompanied by 'access' otherwise these functions are rendered mute as the page will not load. |
| `create`        | Grants privilege to 'create' the specified 'Service'. If the specified 'Service' does not support this it will be transparently ignored. |
| `edit`          | Grants privilege to 'edit' the specified 'Service'. If the specified 'Service' does not support this it will be transparently ignored.  |
| `delete`        | Grants privilege to 'delete' the specified 'Service'. If the specified 'Service' does not support this it will be transparently ignored. |
| `*`             | Grants all of the above (Use very sparingly) |


