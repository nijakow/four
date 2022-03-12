
bool is_dir(string path)
{
    int st = stat(path);
    return (st >= 0) && ((st & 01000) != 0);
}

bool is_file(string path)
{
    int st = stat(path);
        return (st >= 0) && ((st & 01000) == 0);
}

bool exists(string path)
{
    return stat(path) >= 0;
}
