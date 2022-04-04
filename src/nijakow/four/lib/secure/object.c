inherits "/lib/core.c";

/*
 * This is the basic object
 */

use $clone_instance;

any clone(...)
{
	any instance;

	instance = $clone_instance(this);
	instance->create(...);
	return instance;
}

void create()
{
}

void _init()
{
}
