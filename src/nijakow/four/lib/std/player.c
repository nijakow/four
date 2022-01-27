inherits "/std/thing.c";
inherits "/std/container.c";

func line_cb;

void write(...)
{
    if (line_cb != nil)
        call(line_cb, ...);
}

void submit_lines_to(func cb)
{
    this.line_cb = cb;
}

void activate_as(string name)
{
    set_name(name);
    add_IDs(name);
}

bool query_is_heavy() { return true; }

void create()
{
    "/std/thing.c"::create();
    set_properly_named();
}
