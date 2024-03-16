Totpy
=====
Desktop authenticator for generating one-time passwords based on [TOTP Protocol](https://www.rfc-editor.org/rfc/pdfrfc/rfc6238.txt.pdf).

Supports:

- protection of tokens using a password, and optionally keyfile
- sorting of tokens based on category (work, personal, email, ...)
- search based on token category, issuer or account
- adding tokens by scanning a QR code, opening a JPEG image containing a QR code, pasting OTPAuth URI, or manually entering values for required token parameters
- exporting tokens to a file protected by a password or second authenticator's asymmetric key
- importing tokens from a locked export file

![01.png](doc/img/01.png?raw=true)

---

**FIRST STARTUP**

On first startup, the application checks if there is a **.totpy** directory in the user's HOME directory, containing the database with encrypted TOTP tokens.
If the directory does not exist a startup screen is presented where a password needs to be defined, and optionally keyfile selected.

![02.png](doc/img/02.png?raw=true) 

---

**TRANSFERING A SINGLE TOKEN TO A SECOND AUTHENTICATOR APP**

A QR code containing an OTPAuth URI for a token compatible with any 2FA app is presented by clicking on a QR code button on the side of a token panel.

![03.png](doc/img/03.png?raw=true)

---

**EXPORTING TOKENS**

Tokens can be exported to a file for backup or transfer to a second authenticator app.  
The generated export file will be compatible with desktop and mobile (**totpy-mobile-authenticator** repository) version of this authenticator.

**To generate a password-protected export:**

1. select "Export" option in the top menu
2. select tokens you wish to export and click on the export button on the bottom of the UI
3. select "Protect with password" option in the popup dialog that appears and define a password

File **export.json** containing tokens encrypted with AES/GCM will be generated in the current directory.  
This type of export is useful for backing up tokens given that it does not depend on the private key of a specific authenticator, so tokens can be restored after a fresh installation of this application.

![04.png](doc/img/04.png?raw=true)

**To generate an asymmetric-key protected export:**

1. select "Export" option in the top menu
2. select tokens you wish to export and click on the export button on the bottom of the UI
3. select "Protect with public key" option in the popup dialog that appears by pressing a button to scan the public key from a QR code, or a button to load a public key from a file

To view or save the public key of a second authenticator to which you wish to transfer tokens to, select "Show Public Key" dialog in the top menu of that authenticator app.

File **export.json** containing tokens encrypted with AES/GCM will be generated in the current directory.  
This type of export is useful for transfering tokens to a second authenticator app.

![05.png](doc/img/05.png?raw=true)

---

**IMPORTING TOKENS**
  
1. Select "Import" option in the top menu and select the locked export file from which you with to import tokens
2. Select "Clear current database" or "Overwrite existing tokens" options to modify import process and press "IMPORT" button.

- **"Clear current database"** will remove all current tokens before import
- **"Overwrite existing tokens"** will overwrite any token that has the same **Issuer & Account** combination  
If neither of these options is selected error will be shown if a duplicate token appears to prevent accidental overwrites.

![06.png](doc/img/06.png?raw=true)
