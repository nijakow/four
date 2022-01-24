inherits "/secure/object.c";

object the_connection;
func   finish_cb;

object connection() { return the_connection; }

void exit() { call(finish_cb); }

bool exec(func our_cb, string name, ...)
{
    object obj = new(name, the_connection, our_cb, ...);
    if (obj != nil) {
        obj->start();
        return true;
    } else {
        return false;
    }
}

void start()
{
    resume();
}

void create(object connection, func finish_cb)
{
    "/secure/object.c"::create();
    this.the_connection = connection;
    this.finish_cb      = finish_cb;
}
