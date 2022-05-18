inherits "/lib/object.c";
inherits "/lib/stdio.c";

use $nonlocal_exit;

private func _return_cb;

void exit(any value)
{
    $nonlocal_exit(this._return_cb, value);
}

any main(...)
{
    return nil;
}

void _start(...)
{
    exit(main(...));
}

bool exec(func cb, string path, ...)
{
    object instance = new(path, cb);
    if (instance == nil)
        return false;
    else {
        instance->_start(...);
        return true;
    }
}

void _init(func return_callback)
{
    "/lib/object.c"::_init();
    this._return_cb = return_callback;
}
