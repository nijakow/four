inherits "/std/thing.c";
inherits "/std/container.c";

func line_cb;
object frozen_loc;

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

void freeze()
{
    if (frozen_loc == nil) {
        me_act("disappears in a cloud of smoke.\n");
        printf("You disappear in a cloud of smoke.\n");
        frozen_loc = get_parent();
        move_to(nil);
    }
}

void thaw()
{
    if (get_parent() == nil && loc != nil) {
        me_act("appears in a cloud of smoke.\n");
        printf("You appear in a cloud of smoke.\n");
        move_to(frozen_loc);
        frozen_loc = nil;
    }
}

bool query_is_heavy() { return true; }

void create()
{
    "/std/thing.c"::create();
    set_properly_named();
    this.frozen_loc = nil;
}
