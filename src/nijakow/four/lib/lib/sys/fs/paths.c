inherits "/lib/string/split.c";
inherits "/lib/list/list.c";
inherits "/lib/string/string.c";
inherits "/lib/string/split.c";
inherits "/lib/string/substring.c";

use $statics;

string FileSystem_GetWorkingDirectory()
{
    if ($statics()["pwd"] == nil)
        FileSystem_Chdir("/");
    return $statics()["pwd"];
}

bool FileSystem_Chdir(string dir)
{
    $statics()["pwd"] = dir;
    return true;
}

bool FileSystem_SetCurrentDirectory(string dir)
{
    return FileSystem_Chdir(dir);
}


private string FileSystem_Updir(string base)
{
    string  s = "";
    string* l = String_SplitOnChar(base, '/');

    for (int i = 0; i < List_Length(l) - i; i++)
    {
        if (l[i] != "") {
            s = s + "/";
            s = s + l[i];
        }
    }
    if (s == "")
        s = "/";
    return s;
}

private string FileSystem_Resolve1(string base, string dir)
{
    if (dir == "" || dir == ".")
        return base;
    else if (dir == "..")
        return FileSystem_Updir(base);
    else {
        if (base[-1] == '/')
            return base + dir;
        else
            return base + "/" + dir;
    }
}

string FileSystem_Resolve(string base, string path)
{
    if (path[0] == '/')
        return path;

    string* tokens = String_SplitOnChar(path, '/');

    foreach (string dir : tokens)
    {
        base = FileSystem_Resolve1(base, dir);
    }
    return base;
}

string FileSystem_ResolveHere(string path)
{
    return FileSystem_Resolve(FileSystem_GetWorkingDirectory(), path);
}

string FileSystem_Basename(string path)
{
    if (path == "")
        return "/";
    if (path[-1] == '/')
        path = String_IndexBasedSubstring(path, 0, String_Length(path) - 1);
    if (path == "")
        return "/";
    for (int index = String_Length(path) - 1; index >= 0; index--)
    {
        if (path[index] == '/')
            return String_IndexBasedSubstring(path, index + 1, String_Length(path));
    }
    return path;
}
