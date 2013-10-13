# Dastrax
Lead Developer: Tarka L'Herpiniere <tarka@solid.com>.

### About
Dastrax is the heterogeneous portal developed for SOLiD (Reach Holdings USA) to provide services and support to its customers. The original inception of the system was to provide **DAS TRACKING** *(DAS-TRAX)* however, in its second genration the scope of the project was expanded to include customer service capabilities, reporting, resource access etc. 

### License
Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED with classpath exception. For specific library licenses please view the dependencies package. 

### Technology Stack
###### Language
1. Java EE
  * Version 7
  * Build 25

###### Build Tool
1. Maven
  * Version 3

###### Container
1. Glassfish
  * Version 4
  * Build 89
2. Mojarra
  * Version 2.2

###### External Dependencies
1. Amazon Web Services

###### Versioning
1. Git

###### IDE
1. Netbeans
  * Version 7.2 *(non-compatible)*
  * Version 7.3 *(non-compatible)*
  * Version 7.3.1
  * Version 7.4-beta

### Build Information
*TODO*

### Deployment Information
*TODO*

### Versioning Information
The application uses [**Semantic Versioning**](http://semver.org/) to version its releases. The following is an extract explanation of the versioning system used;

#####Semantic Versioning Specification (SemVer)
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](http://tools.ietf.org/html/rfc2119).

1. Software using Semantic Versioning MUST declare a public API. This API could be declared in the code itself or exist strictly in documentation. However it is done, it should be precise and comprehensive.

2. A normal version number MUST take the form X.Y.Z where X, Y, and Z are non-negative integers, and MUST NOT contain leading zeroes. X is the major version, Y is the minor version, and Z is the patch version. Each element MUST increase numerically. For instance: 1.9.0 -> 1.10.0 -> 1.11.0.

3. Once a versioned package has been released, the contents of that version MUST NOT be modified. Any modifications MUST be released as a new version.

4. Major version zero (0.y.z) is for initial development. Anything may change at any time. The public API should not be considered stable.

5. Version 1.0.0 defines the public API. The way in which the version number is incremented after this release is dependent on this public API and how it changes.

6. Patch version Z (x.y.Z | x > 0) MUST be incremented if only backwards compatible bug fixes are introduced. A bug fix is defined as an internal change that fixes incorrect behavior.

7. Minor version Y (x.Y.z | x > 0) MUST be incremented if new, backwards compatible functionality is introduced to the public API. It MUST be incremented if any public API functionality is marked as deprecated. It MAY be incremented if substantial new functionality or improvements are introduced within the private code. It MAY include patch level changes. Patch version MUST be reset to 0 when minor version is incremented.

8. Major version X (X.y.z | X > 0) MUST be incremented if any backwards incompatible changes are introduced to the public API. It MAY include minor and patch level changes. Patch and minor version MUST be reset to 0 when major version is incremented.

9. A pre-release version MAY be denoted by appending a hyphen and a series of dot separated identifiers immediately following the patch version. Identifiers MUST comprise only ASCII alphanumerics and hyphen [0-9A-Za-z-]. Identifiers MUST NOT be empty. Numeric identifiers MUST NOT include leading zeroes. Pre-release versions have a lower precedence than the associated normal version. A pre-release version indicates that the version is unstable and might not satisfy the intended compatibility requirements as denoted by its associated normal version. Examples: 1.0.0-alpha, 1.0.0-alpha.1, 1.0.0-0.3.7, 1.0.0-x.7.z.92.

10. Build metadata MAY be denoted by appending a plus sign and a series of dot separated identifiers immediately following the patch or pre-release version. Identifiers MUST comprise only ASCII alphanumerics and hyphen [0-9A-Za-z-]. Identifiers MUST NOT be empty. Build metadata SHOULD be ignored when determining version precedence. Thus two versions that differ only in the build metadata, have the same precedence. Examples: 1.0.0-alpha+001, 1.0.0+20130313144700, 1.0.0-beta+exp.sha.5114f85.

11. Precedence refers to how versions are compared to each other when ordered. Precedence MUST be calculated by separating the version into major, minor, patch and pre-release identifiers in that order (Build metadata does not figure into precedence). Precedence is determined by the first difference when comparing each of these identifiers from left to right as follows: Major, minor, and patch versions are always compared numerically. Example: 1.0.0 < 2.0.0 < 2.1.0 < 2.1.1. When major, minor, and patch are equal, a pre-release version has lower precedence than a normal version. Example: 1.0.0-alpha < 1.0.0. Precedence for two pre-release versions with the same major, minor, and patch version MUST be determined by comparing each dot separated identifier from left to right until a difference is found as follows: identifiers consisting of only digits are compared numerically and identifiers with letters or hyphens are compared lexically in ASCII sort order. Numeric identifiers always have lower precedence than non-numeric identifiers. A larger set of pre-release fields has a higher precedence than a smaller set, if all of the preceding identifiers are equal. Example: 1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-alpha.beta < 1.0.0-beta < 1.0.0-beta.2 < 1.0.0-beta.11 < 1.0.0-rc.1 < 1.0.0.

### Permissions
Permissions are granted to user accounts to allow them to access and perform restricted services. Permissions work by granting access to a restricted services, this means that by default an account has the minimum 'public' permissions and additional permissions need to be granted in order to allow that user to access and manipulate the desired services.

Permissions are granted in the following format;
_SERVICES:FUNCTIONS_
Where the specified functions are granted for the specified services.

Both services and functions can be specified as comma separate lists with no spaces;
_SERVICE,SERVICE,SERVICE:FUNCTION,FUNCTION_

All services supplied on the left of the colon (:) will be granted the function on the right of the colon. So the following would grant a user permission to view (access) and create both sites and accounts;
_account,site:access,create_

To specify different levels of granularity within a users permissions multiple permissions can be granted. The following would grant 'access' and 'create' functions to 'account' services, but only 'access' to 'sites';
_account:access,create_
_site:access_

The * can be specified as a wildcard for both services and functions.

Permissions are bound by the users account type (metier). Granting a VAR account _*:*_ ({ALL SERVICES}:{ALL FUNCTIONS}) does not mean that they have the same access rights as an administrator granted *:*. It simply implies that within the VAR user account they have all the functions available in all the services that are available as a VAR.

| Services      | Description        |
|-------------- | ------------------ |
| account       | User accounts.     |
| ticket        | Support tickets.    |
| dmsticket     | Automated tickets.  |
| company       | VAR and Client companies. |
| site          | DAS installation sites. |
| report-basic  | DAS reporting. |
| monitor-basic | DAS and DMS overall live monitoring. |
| monitor-adv   | DAS specific device live monitoring. |
| university    | SOLiD university. |
| knowledge     | Egnyte file storage. |
| rma           | Return material authorization. |
| permission    | User permissions. By default the 'account' Service allows all account features to be manipulated EXCEPT permissions. |
| *             | All of the above (Use very sparingly) |

| Functions     | Description        |
|-------------- | ------------------ |
| access        | Grants privilege to 'access' the specified 'Service'. 'create, edit, delete' functions must be accompanied by 'access' otherwise these functions are rendered mute as the page will not load. |
| create        | Grants privilege to 'create' the specified 'Service'. If the specified 'Service' does not support this it will be transparently ignored. eg. 'university' does not support 'create' however, the permission 'university:create' is valid and will simply be ignored |
| edit          | Grants privilege to 'edit' the specified 'Service'. If the specified 'Service' does not support this it will be transparently ignored. eg. 'university' does not support 'edit' however, the permission 'university:edit' is valid and will simply be ignored |
| delete        | Grants privilege to 'delete' the specified 'Service'. If the specified 'Service' does not support this it will be transparently ignored. eg. 'university' does not support 'delete' however, the permission 'university:edit' is valid and will simply be ignored |
| *             | Grants all of the above (Use very sparingly) |


### Contact
| Type      | Name               | Role                | Telephone    | Email               | Address                              |
|---------- | ------------------ | ------------------- | ------------ | ------------------- | ------------------------------------ |
| General   | Mike Wing          | NOC/Support Manager | 408-639-0426 | mike.wing@solid.com | 617 N MARY AVE, SUNNYVALE, CA, 94085 |
| Technical | Tarka L'Herpiniere | Lead Developer      | 408-400-3802 | tarka@solid.com     | 617 N MARY AVE, SUNNYVALE, CA, 94085 |

