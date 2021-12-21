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

void mode_normal()     { write("\{RESET\}"); }
void mode_red()        { write("\{RED\}"); }
void mode_green()      { write("\{GREEN\}"); }
void mode_blue()       { write("\{BLUE\}"); }
void mode_yellow()     { write("\{YELLOW\}"); }
void mode_black()      { write("\{BLACK\}"); }
void mode_italic()     { write("\{ITALIC\}"); }
void mode_bold()       { write("\{BOLD\}"); }
void mode_underscore() { write("\{UNDERSCORED\}"); }

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
