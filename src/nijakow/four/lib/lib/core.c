use $type;
use $the_object;
use $is_initialized;
use $set_initialized;
use $call;

string Object_Type(any obj)
{
    return $type(obj);
}

object the(string id)
{
    object obj = $the_object(id);

    if (obj != nil)
    {
        if (!$is_initialized(obj)) {
            $set_initialized(obj);
            obj->_init();
        }
    }
    return obj;
}

any _new(string blueprint, ...)
{
    object obj = the(blueprint);
    if (obj != nil)
        return obj->clone(...);
    return nil;
}

any call(any f, ...)
{
    return $call(f, ...);
}
