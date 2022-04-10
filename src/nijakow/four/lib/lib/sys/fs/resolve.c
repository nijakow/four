inherits "/lib/string/split.c";
inherits "/lib/list/list.c";
inherits "/lib/string/string.c";
inherits "/lib/string/split.c";
inherits "/lib/string/substring.c";

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
