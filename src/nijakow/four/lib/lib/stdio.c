use $statics;

private object terminal()
{
    return $statics()["terminal"];
}

void printf(string format, ...)
{
    terminal()->printf(format, ...);
}

void prompt(func callback, string prompt, ...)
{
    terminal()->prompt(format, ...);
}

void password(func callback, string prompt, ...)
{
    terminal()->password(format, ...);
}
