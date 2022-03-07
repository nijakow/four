
string updir(string base)
{
    list l = spliton(base, this::isslash);
    string s = "";
    for (int i = 0; i < length(l) - 1; i++)
    {
        if (l[i] != "") {
            s = s + "/";
            s = s + l[i];
        }
    }
    if (s == "") s = "/";
    return s;
}

string resolv1(string base, string dir)
{
    if (dir == "" || dir == ".") return base;
    else if (dir == "..") return updir(base);
    else {
        if (base[-1] == '/') return base + dir;
        else return base + "/" + dir;
    }
}

string resolve(string base, string path)
{
    if (path[0] == '/') return path;

    list toks = spliton(path, this->isslash);

    foreach (string dir : toks)
    {
        base = resolv1(base, dir);
    }
    return base;
}

string basename(string path)
{
    while (endswith(path, '/'))
        path = substr(path, 0, strlen(path) - 1);
    if (path == "")
        return "/";
    int index = rsfind(path, '/');
    if (index < 0)
        return path;
    else
        return substr(path, index + 1, strlen(path));
}
