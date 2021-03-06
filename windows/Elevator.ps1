# Elevator.ps1
# -------------------------------------
# Takes a PowerShell script (ps1) and runs it as Administrator
# 
# Code taken from http://blogs.msdn.com/b/virtual_pc_guy/archive/2010/09/23/a-self-elevating-powershell-script.aspx

# Get the ID and security principal of the current user account
$myWindowsID=[System.Security.Principal.WindowsIdentity]::GetCurrent()
$myWindowsPrincipal=new-object System.Security.Principal.WindowsPrincipal($myWindowsID)

# Get the security principal for the Administrator role
$adminRole=[System.Security.Principal.WindowsBuiltInRole]::Administrator

# Check to see if we are currently running "as Administrator"
if ($myWindowsPrincipal.IsInRole($adminRole))
   {
   # We are running "as Administrator" - so change the title and background color to indicate this
   $Host.UI.RawUI.WindowTitle=$myInvocation.MyCommand.Definition + "(Elevated)"
   $Host.UI.RawUI.BackgroundColor="DarkBlue"
   clear-host
   }
else
   {
   # We are not running "as Administrator" - so relaunch as administrator
   
   # Create a new process object that starts PowerShell
   $newProcess=New-Object System.Diagnostics.ProcessStartInfo "PowerShell";
   
   # Specify the current script path, name and arguments as a parameter
   $newProcess.Arguments=$myInvocation.MyCommand.Definition + " " + $args;
   
   # Indicate that the process should be elevated
   $newProcess.Verb="runas";
   
   # Start the new process
   [System.Diagnostics.Process]::Start($newProcess);
   
   # Exit from the current, unelevated, process
   exit
   }

# Run your code that needs to be elevated here
$psScript=$args[0]
$psArgs=$args[1..$args.count]

Write-Host "Executing: " $psScript $psArgs
. $psScript $psArgs

# End
Write-Host -NoNewLine "Press any key to continue..."
$null=$Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
