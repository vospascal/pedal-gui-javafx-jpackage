#help
If you want to develop the software furter please create a pull request or create an issue


# pedal-gui-javafx-jpackage

----------------------------

### idea
ctrl + ctrl = run commando

----------------------------
### Powershell tips
make sure the $env:JAVA_HOME is set properly inside env.ps1

----------------------------
###Powershell restrictions 
run `Get-ExecutionPolicy` should be returning `Restricted`

open powershell with admin rights
run `Set-ExecutionPolicy unrestricted`

you will see this
`Execution Policy Change
The execution policy helps protect you from scripts that you do not trust. Changing the execution policy might expose
you to the security risks described in the about_Execution_Policies help topic at
https:/go.microsoft.com/fwlink/?LinkID=135170. Do you want to change the execution policy?
[Y] Yes  [A] Yes to All  [N] No  [L] No to All  [S] Suspend  [?] Help (default is "N"):`

Press `A`

if you run `Get-ExecutionPolicy` should be returning `Unrestricted`

now it should work
