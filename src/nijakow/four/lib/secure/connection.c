inherit "/secure/object.c";

use $write;
use $close;
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

void mode_normal()     { write("\{N\}"); }
void mode_red()        { write("\{R\}"); }
void mode_green()      { write("\{G\}"); }
void mode_blue()       { write("\{B\}"); }
void mode_black()      { write("\{0\}"); }
void mode_italic()     { write("\{i\}"); }
void mode_bold()       { write("\{b\}"); }
void mode_underscore() { write("\{u\}"); }

void receive(string text)
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

void close()
{
    $close(port);
}

void create(any the_port)
{
	"/secure/object.c"::create();
	port = the_port;
	callback = nil;
	$on_receive(port, this::receive);
	$on_disconnect(port, this::handle_disconnect);
}
