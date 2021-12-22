inherit "/secure/object.c";

/*
 *    T r e e   S t r u c t u r e 
 */

object parent;
object sibling;
object children;

object get_parent()   { return parent;  }
object get_sibling()  { return sibling; }
object get_children() { return children; }

object get_location() { return get_parent(); }


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


/*
 *    D e s c r i p t i v e   P r o p e r t i e s
 */

string short_desc;
string long_desc;

string get_short() { return short_desc; }
string get_long()  { return long_desc;  }

void set_short(string s) { short_desc = s; }
void set_long(string l)  { long_desc  = l; }


/*
 *    E v e n t s
 */

void evt_entering(object obj) {}
void evt_leaving(object obj) {}

/*
 *    C h e c k s
 */

bool is_container;

bool check_enter(object obj) { return is_container; }
bool check_exit(object obj)  { return true;         }


/*
 *    A c t i o n s   a n d   M o v e m e n t
 */

void on_act(object actor, ...)
{
    if (actor != this)
        write(...);
    for (object child = get_children();
         child != nil;
         child = child->get_sibling())
    {
        child->on_act(actor, ...);
    }
}

void act(...)
{
    object location = get_location();
    
    if (location != nil) {
        location->on_act(this, ...);
    } else {
        this->on_act(this, ...);
    }
}

bool go_to(object location)
{
    object current = get_location();
    
    if (current != nil && !check_exit(this))
        return false;
    if (location != nil && !check_enter(this))
        return false;
    
    if (location != nil)
        location->evt_entering(this);
    move_to(location);
    if (current != nil)
        current->evt_leaving(this);
    
    return true;
}


/*
 *    R e a c t i o n s   a n d   F i n d i n g
 */

list names;

bool reacts(list words)
{
    for (int x = 0; x < length(words); x++)
        if (!member(names, word[x]))
            return false;
    return true;
}
void add_name(string word) { append(names, word);        }


/*
 *    M i s c e l l a n e o u s
 */

void write(...) {}

bool inhibit_create_on_init() { return 0; }


void create()
{
    "/secure/object.c"::create();
    parent = nil;
    sibling = nil;
    children = nil;
    set_short("<error>");
    set_long("<error>");
}

void _init()
{
    "/secure/object.c"::_init();
    is_container = true;
    names = {};
    if (!inhibit_create_on_init()) {
        create();
    }
}
