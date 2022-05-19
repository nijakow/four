#include "/lib/list/list.c"
#include "/lib/string/split.c"

/*
 *     T r e e   S e c t i o n
 */

use $get_parent;
use $get_sibling;
use $get_children;
use $move_to;

private bool is_opaque;

object get_parent() { return $get_parent(this); }
object get_sibling() { return $get_sibling(this); }
object get_children() { return $get_children(this); }

void move_to(object target)
{
    $move_to(target);
}

bool query_is_opaque() { return this.is_opaque; }
void enable_opaque() { this.is_opaque = true; }
void disable_opaque() { this.is_opaque = false; }

void collect_direct_children(object* list)
{
    object child;

    for (child = get_children(); child != nil; child = child->get_sibling())
    {
        List_Append(list, child);
    }
}

void collect_children(object* list, bool all)
{
    object child;

    for (child = get_children(); child != nil; child = child->get_sibling())
    {
        List_Append(list, child);
        if (all || !child->query_is_opaque())
            child->collect_children(list, all);
    }
}

void collect_here(object* list, bool all)
{
    if (get_parent() == nil || query_is_opaque()) {
        collect_children(list, all);
    } else {
        if (get_parent() != nil)
            get_parent()->collect_here(list, all);
    }
}

void find_here(object* results, string name, bool all)
{
    object* here;
    string* ids;
    bool    found;

    ids = String_SplitOnWhitespace(name);
    here = {};
    collect_here(here, all);
    for (object obj : here)
    {
        found = true;
        for (string s : ids)
        {
            if (!obj->reacts_to_id(s))
            {
                found = false;
                break;
            }
        }
        if (found)
            List_Append(results, obj);
    }
}

void reset()
{
    this.is_opaque = false;
}

void _init()
{
}
