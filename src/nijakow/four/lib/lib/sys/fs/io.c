use $filetext;
use $filetext_set;

string FileSystem_ReadFile(string path)
{
    return $filetext(path);
}

bool FileSystem_WriteFile(string path, string text)
{
    return $filetext_set(path, text);
}
