#include "/lib/object.c"
#include "/lib/list/list.c"

/*
 *     L o c a t i o n   S e c t i o n
 */

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

/*
 *     N a m e   S e c t i o n
 */

private string short_description;
private string long_description;
private string* identifiers;

string get_short() { return this.short_description; }
void set_short(string new_short) { this.short_description = new_short; }

string get_long() { return this.long_description; }
void set_long(string new_long) { this.long_description = new_long; }

void add_id(string id) { List_Append(identifiers, id); }

/*
 *     C o m m a n d   S e c t i o n
 */

bool obey(string command)
{
    return false;
}

/*
 *     E v e n t   S e c t i o n
 */

void receive(string event)
{
}

/*
 *     C o n s t r u c t o r   S e c t i o n
 */
void reset()
{
}

void _init()
{
    "/lib/object.c"::_init();
    set_short("a thing");
    this.identifiers = {};
}
