use $mkdir;
use $touch;
use $rm;
use $filetext;
use $filetext_set;
use $filechildren;
use $recompile;

bool FileSystem_CreateDirectory(string path)
{
    return $mkdir(path);
}

bool FileSystem_CreateFile(string path)
{
    return $touch(path);
}

bool FileSystem_DeleteFile(string path)
{
    return $rm(path);
}

string FileSystem_ReadFile(string path)
{
    return $filetext(path);
}

bool FileSystem_WriteFile(string path, string text)
{
    return $filetext_set(path, text);
}

string* FileSystem_GetFilesIn(string path)
{
    return $filechildren(path);
}

string FileSystem_Compile(path)
{
    return $recompile(path);
}
