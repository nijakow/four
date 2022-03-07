
bool is_dir(string path)
{
    return ls(path) != nil;
}

bool is_file(string path)
{
    return cat(path) != nil;
}

bool exists(string path)
{
    return stat(path) >= 0;
}
