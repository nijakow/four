inherits "/lib/object.c";
inherits "/lib/stdio.c";

use $exec;
use $exit;

void exit(any value)
{
    $exit(value);
}

any main(...)
{
    return nil;
}

void _start(...)
{
    exit(main(...));
}

private void run_instance(object instance, ...)
{
    instance->_start(...);
}

bool exec(func cb, string path, ...)
{
    object instance = new(path);
    if (instance == nil)
        return false;
    else {
        call(cb, $exec(this::run_instance, instance, ...));
        return true;
    }
}

void _init()
{
    "/lib/object.c"::_init();
}
