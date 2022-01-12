/*
 *    T r e e   S t r u c t u r e
 */

use $get_parent;
use $get_sibling;
use $get_children;
use $move_to;

object get_parent()   { return $get_parent();  }
object get_sibling()  { return $get_sibling(); }
object get_children() { return $get_children(); }

object get_location() { return get_parent(); }

void move_to(object new_parent)
{
    $move_to(new_parent);
}

bool contains(object obj)
{
    for (object child = get_children();
         child != nil;
         child = child->get_sibling())
    {
        if (child->contains_or_is(obj))
            return true;
    }
    return false;
}

bool contains_or_is(object obj)
{
    if (obj == this)
        return true;
    return contains(obj);
}

object find_common_parent(object obj)
{
    for (object i = this; i != nil; i = i->get_parent())
    {
        for (object j = this; j != nil; j = j->get_parent())
        {
            if (i == j)
                return i;
        }
    }
    return nil;
}

void _add_objects(list the_list)
{
    append(the_list, this);
    for (object obj = get_children();
         obj != nil;
         obj = obj->get_sibling())
    {
        obj->_add_objects(the_list);
    }
}

list all_children()
{
    list objects = {};
    _add_objects(objects);
    return objects;
}

list objects_here()
{
    list objects = {};
    object loc = get_location();
    if (loc != nil)
        loc->_add_objects(objects);
    return objects;
}

list visible_objects_here()
{
    return objects_here();
}


void create()
{
}
