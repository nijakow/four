use $finduser;
use $getshell;
use $chsh;
use $chpass;
use $isactive;
use $getuid;

string User_Whoami()
{
    return $getuid();
}

bool User_Exists(string name)
{
    return $finduser(name) != nil;
}

bool User_IsRoot(string name)
{
    return name == "root";
}

string User_GetShell(string name)
{
    return $getshell(name);
}

bool User_ChangeShell(string name, string shell)
{
    return $chsh(name, shell);
}

bool User_ChangePassword(string user, string pass)
{
    return $chpass(user, pass);
}

bool User_IsActive(string user)
{
    return $isactive(user);
}
