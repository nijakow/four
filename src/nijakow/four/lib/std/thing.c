#include "/lib/object.c"

use $get_parent;
use $get_sibling;
use $get_children;
use $move_to;

object get_parent() { return $get_parent(self); }
object get_sibling() { return $get_sibling(self); }
object get_children() { return $get_children(self); }

void move_to(object target)
{
    $move_to(target);
}

bool obey(string command)
{
    return false;
}

void receive(string event)
{
}

void reset()
{
}

void _init()
{
    "/lib/object.c"::_init();
}
