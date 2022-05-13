inherits "/lib/core.c";

/*
 * This is the basic object
 */

use $clone_instance;

any clone(...)
{
	any instance;

	instance = $clone_instance(this);
	instance->_init(...);
	return instance;
}

void _pre_init()
{
}

void _init()
{
}
