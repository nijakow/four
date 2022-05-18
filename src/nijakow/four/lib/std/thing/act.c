#include "/lib/io/sprintf.c"

void act(string fmt, ...)
{
    object* here = {};
    string  msg;

    msg = sprintf(fmt, ...);
    this.collect_here(here, false);
    for (object obj : here)
    {
        if (obj != this)
            obj->receive(msg);
    }
}

void reset()
{
}

void _init()
{
}
