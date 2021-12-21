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

void mode_normal()     { write("\aN"); }
void mode_red()        { write("\aR"); }
void mode_green()      { write("\aG"); }
void mode_blue()       { write("\aB"); }
void mode_black()      { write("\a0"); }
void mode_italic()     { write("\ai"); }
void mode_bold()       { write("\ab"); }
void mode_underscore() { write("\au"); }

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
