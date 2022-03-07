use $filetext;
use $filetext_set;
use $touch;
use $mkdir;
use $rm;
use $mv;
use $recompile;

string cat(string path)
{
    return $filetext(path);
}

bool echo_into(string path, string text)
{
    return $filetext_set(path, text);
}

bool touch(string path)
{
    return $touch(path);
}

bool mkdir(string path)
{
    return $mkdir(path);
}

bool rm(string path)
{
    return $rm(path);
}

bool mv(string from, string to)
{
    return $mv(from, to);
}

bool recompile(string path, func cb)
{
    return $recompile(path, cb);
}
