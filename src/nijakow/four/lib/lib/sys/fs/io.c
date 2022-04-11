use $filetext;
use $filetext_set;
use $filechildren;

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
