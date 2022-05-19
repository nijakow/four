use $statics;

object terminal()
{
    return $statics()["terminal"];
}

void printf(string format, ...)
{
    terminal()->printf(format, ...);
}

string prompt(string format, ...)
{
    return terminal()->prompt(format, ...);
}

string password(string format, ...)
{
    return terminal()->password(format, ...);
}
