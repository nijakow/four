use $stat;
use $checkexec;

int FileSystem_Stat(string path)
{
    return $stat(path);
}

bool FileSystem_Exists(string path)
{
    return (FileSystem_Stat(path) >= 0);
}

bool FileSystem_IsTextFile(string path)
{
    int st = FileSystem_Stat(path);
    return ((st >= 0) && ((st & 01000) == 0));
}

bool FileSystem_IsDirectory(string path)
{
    int st = FileSystem_Stat(path);
    return ((st >= 0) && ((st & 01000) != 0));
}

bool FileSystem_IsExecutable(string path)
{
    return $checkexec(path);
}
