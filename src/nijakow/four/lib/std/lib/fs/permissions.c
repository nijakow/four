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

bool chmod(string path, int flags)
{
    return $chmod(path, flags);
}

bool chown(string path, string uid)
{
    return $chown(path, uid);
}

bool chgrp(string path, string gid)
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
