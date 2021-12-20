inherit "/secure/object.c";

use $write;
use $on_receive;
use $on_disconnect;

any port;
func callback;

void prompt(func cb, ...)
{
    callback = cb;
    write(...);
}

void write(...)
{
    while (va_count)
    {
        $write(port, va_next);
    }
}

void receive(any text)
{
    func _cb;

    if (callback) {
    	_cb = callback;
    	callback = nil;
        call(_cb, trim(text));
    }
}

void handle_disconnect()
{
	log("Disconnect!\n");
}

void create(any the_port)
{
	"/secure/object.c"::create();
	port = the_port;
	callback = nil;
	$on_receive(port, this::receive);
	$on_disconnect(port, this::handle_disconnect);
}
