inherits "/secure/object.c";

object the_connection;
func   finish_cb;

object connection() { return the_connection; }

void exit() { call(finish_cb); }

void exec(func our_cb, string name, ...)
{
    new(name, the_connection, our_cb, ...)->start();
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
