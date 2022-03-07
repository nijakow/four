use $isactive;
use $chpass;
use $getshell;
use $chsh;

bool isactive(string name)
{
    return $isactive(name);
}

bool chpass(string user, string pass)
{
    return $chpass(user, pass);
}

string getshell(string user)
{
    return $getshell(user);
}

bool chsh(string user, string shell)
{
    return $chsh(user, shell);
}

bool isroot()
{
    return getuid() == finduser("root");
}
