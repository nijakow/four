/*
 *     E v e n t   S e c t i o n
 */

private func event_cb;

void receive(string event)
{
    if (this.event_cb != nil)
    {
        call(this.event_cb, event);
    }
}

void set_event_callback(func cb)
{
    this.event_cb = cb;
}

void reset()
{
}

void _init()
{
    this.event_cb = nil;
}
