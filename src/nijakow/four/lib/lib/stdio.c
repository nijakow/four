use $statics;

private object terminal()
{
    return $statics()["terminal"];
}

void printf(string format, ...)
{
    terminal()->printf(format, ...);
}

void prompt(func callback, string format, ...)
{
    terminal()->prompt(callback, format, ...);
}

void password(func callback, string format, ...)
{
    terminal()->password(callback, format, ...);
}
