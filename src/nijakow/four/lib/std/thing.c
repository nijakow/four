inherit "/secure/object.c";

object parent;
object sibling;
object children;

object get_parent()   { return parent;  }
object get_sibling()  { return sibling; }
object get_children() { return children; }

void remove_child(object child)
{
    if (children == child) {
        children = child->get_sibling();
    } else {
        for (object c = children; c != nil; c = c->get_sibling)
        {
            if (c->get_sibling() == child) {
                c->sibling = child->get_sibling();
                break;
            }
        }
    }
}

void move_to(object new_parent)
{
    if (parent != new_parent)
    {
        if (parent != nil) {
            parent->remove_child(this);
            parent = nil;
        }
    
        if (new_parent != nil) {
            parent = new_parent;
            sibling = new_parent->children;
            new_parent->children = this;
        }
    }
}


string short_desc;
string long_desc;

string get_short() { return short_desc; }
string get_long()  { return long_desc;  }

void set_short(string s) { short_desc = s; }
void set_long(string l)  { long_desc  = l; }


void evt_entering(object obj) {}
void evt_leaving(object obj) {}

void write(...) {}


void create()
{
    "/secure/object.c"::create();
    parent = nil;
    sibling = nil;
    children = nil;
    set_short("<error>");
    set_long("<error>");
}
