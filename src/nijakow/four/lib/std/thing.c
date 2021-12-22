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
        for (object c = children; c != nil; c = c->get_sibling())
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


/*
 *    D e s c r i p t i v e   P r o p e r t i e s
 */

bool   is_properly_named;
string article;
string name;
string long_desc;

string get_name()         { return name; }
void   set_name(string n) { name = n; }

string get_short()
{
    if (is_properly_named)
        return get_name();
    return article + " " + get_name();
}

string get_long()         { return long_desc;  }
void   set_long(string l) { long_desc  = l; }

string get_the_short()
{
    if (is_properly_named)
        return get_name();
    return "the " + get_name();
}

void set_properly_named() { is_properly_named = true; }


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

void me_act(...)
{
    act(get_short(), " ", ...);
}

bool act_goto(object location)
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

bool act_take(object obj)
{
    /*
     * TODO: Let the object veto against being taken!
     *       (e.g. if the object is inside of a player)
     *       in this case, the object asks the container if
     *       it may be removed (players never allow that).
     *                               - nijakow
     */
    if (this->contains_or_is(obj)) {
        return false;
    } else {
        me_act("takes ", obj->get_short(), ".\n");
        obj->move_to(this);
        return true;
    }
}

bool act_drop(object obj)
{
    /*
     * TODO: Let the object veto against being dropped!
     *       (e.g. if the object is inside of a player)
     *       in this case, the object asks the container if
     *       it may be inserted (players never allow that).
     *                               - nijakow
     */
    if (!this->contains(obj)) {
        return false;
    } else {
        me_act("drops ", obj->get_short(), ".\n");
        obj->move_to(get_location());
    }
}


/*
 *    R e a c t i o n s   a n d   F i n d i n g
 */

list names;

bool reacts(list words)
{
    for (int x = 0; x < length(words); x++)
    {
        if (!member(names, words[x]))
            return false;
    }
    return length(words) != 0;
}

void add_names(...)
{
    while (va_count > 0)
    {
        append(names, va_next);
    }
}

void _find_thing(list names, list elems)
{
    if (this->reacts(names))
        append(elems, this);
    for (object child = get_children();
         child != nil;
         child = child->get_sibling())
    {
        child->_find_thing(names, elems);
    }
}

list find_thing_here(string name)
{
    list elems = {};
    get_location()->_find_thing(split(name), elems);
    return elems;
}

list find_thing(string name)
{
    list elems = {};
    _find_thing(split(name), elems);
    return elems;
}


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
    is_properly_named = false;
    article = "a";
    set_name("<error>");
    set_long("<error>");
    names = {};
}

void _init()
{
    "/secure/object.c"::_init();
    is_container = true;
    if (!inhibit_create_on_init()) {
        create();
    }
}
