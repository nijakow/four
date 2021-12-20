inherit "/secure/stdlib.c";

/*
 * This is the basic object
 */

use $clone_instance;

void create()
{
}

any clone(...)
{
	any instance;

	instance = $clone_instance(this);
	instance->create(...);
	return instance;
}
