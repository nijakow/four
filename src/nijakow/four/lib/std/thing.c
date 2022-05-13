#include "/lib/object.c"
#include "/lib/list/list.c"

/*
 *     L o c a t i o n   S e c t i o n
 */

use $get_parent;
use $get_sibling;
use $get_children;
use $move_to;

object get_parent() { return $get_parent(this); }
object get_sibling() { return $get_sibling(this); }
object get_children() { return $get_children(this); }

void move_to(object target)
{
    $move_to(target);
}

/*
 *     N a m e   S e c t i o n
 */

private string short_text;
private string long_text;
private string desc;
private string* identifiers;

string get_short() { return this.short_text; }
void set_short(string new_short) { this.short_text = new_short; }

string get_long() { return this.long_text; }
void set_long(string new_long) { this.long_text = new_long; }

string get_desc() { return this.desc; }
void set_desc(string new_desc) { this.desc = new_desc; }

void add_id(string id) { List_Append(this.identifiers, id); }

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
    set_short("a thing");
    set_long("A thing.");
    set_desc("This is a thing. It has no description.");
    this.identifiers = {};
}

void _init()
{
    "/lib/object.c"::_init();
    reset();
}
