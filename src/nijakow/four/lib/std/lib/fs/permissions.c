use $getown;
use $getgrp;
use $chmod;
use $chown;
use $chgrp;
use $checkexec;
use $stat;

string getown(string path)
{
    return $getown(path);
}

string getgrp(string path)
{
    return $getgrp(path);
}

int chmod(string path, int flags)
{
    return $chmod(path, flags);
}

int chown(string path, string uid)
{
    return $chown(path, uid);
}

int chgrp(string path, string gid)
{
    return $chgrp(path, gid);
}

bool checkexec(string path)
{
    return $checkexec(path);
}

int stat(string path)
{
    return $stat(path);
}
