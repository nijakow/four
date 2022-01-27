inherits "/secure/object.c";

object the_connection;
func   finish_cb;
object _me;

object connection() { return the_connection; }
object me() { return _me; }
void set_me(object value) { this._me = value; }

void exit() { call(finish_cb); }

bool exec(func our_cb, string name, ...)
{
    object obj = new(name, the_connection, our_cb, me(), ...);
    if (obj != nil) {
        obj->start();
        return true;
    } else {
        return false;
    }
}

bool execapp(func our_cb, string name, string working_dir, list args, ...)
{
    return exec(our_cb, name, working_dir, args, ...);
}

void start()
{
    resume();
}

void create(object connection, func finish_cb, object the_me)
{
    "/secure/object.c"::create();
    this.the_connection = connection;
    this.finish_cb      = finish_cb;
    this._me            = the_me;
}
