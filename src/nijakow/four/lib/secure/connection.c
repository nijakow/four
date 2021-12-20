inherit "/secure/object.c";

use $write;
use $on_receive;
use $on_disconnect;

any port;
any func;

void prompt(any the_func, ...)
{
    func = the_func;
    write(...);
}

void write(...)
{
    while (va_count) {
        $write(port, va_next);
    }
}

void receive(any text)
{
    any _func;

    if (func) {
    	_func = func;
    	func = nil;
        call(_func, trim(text));
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
	func = nil;
	$on_receive(port, this::receive);
	$on_disconnect(port, this::handle_disconnect);
}
