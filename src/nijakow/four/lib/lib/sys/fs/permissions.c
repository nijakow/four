use $getown;
use $getgrp;
use $chown;
use $chgrp;
use $chmod;

string FileSystem_GetFileOwner(string path)
{
    return $getown(path);
}

string FileSystem_GetFileGroup(string path)
{
    return $getgrp(path);
}

bool FileSystem_SetFileOwner(string path, string owner)
{
    return $chown(path, owner);
}

bool FileSystem_SetFileGroup(string path, string group)
{
    return $chgrp(path, group);
}

bool FileSystem_SetFileFlags(string path, int flags)
{
    return $chmod(path, flags);
}
