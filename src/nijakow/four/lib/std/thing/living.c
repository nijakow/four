#include "/lib/list/list.c"

use $fork;
use $sleep;

private func* living_callbacks;

void living_tick()
{
    while (this.living_callbacks != nil)
    {
        for (func cb : this.living_callbacks)
            call(cb);
        $sleep(5000);
    }
}

void enable_living(func callback)
{
    if (this.living_callbacks == nil)
    {
        this.living_callbacks = {};
        // TODO: Make a global tick thread/fiber
        $fork(this::living_tick);
    }
    List_Append(this.living_callbacks, callback);
}

void disable_living()
{
    this.living_callback = nil;
}

void reset()
{
}

void _init()
{
}
