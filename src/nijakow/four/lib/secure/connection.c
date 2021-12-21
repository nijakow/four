inherit "/secure/object.c";

use $write;
use $close;
use $on_receive;
use $on_disconnect;

any port;
func callback;
string line;

void prompt(func cb, ...)
{
    callback = cb;
    write("\{.");
    write(...);
    write("\}");
}

void password(func cb, ...)
{
    callback = cb;
    write("\{?");
    write(...);
    write("\}");
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
void mode_bg_red()     { write("\{BG_RED\}"); }
void mode_bg_green()   { write("\{BG_GREEN\}"); }
void mode_bg_blue()    { write("\{BG_BLUE\}"); }
void mode_bg_yellow()  { write("\{BG_YELLOW\}"); }
void mode_bg_black()   { write("\{BG_BLACK\}"); }
void mode_italic()     { write("\{ITALIC\}"); }
void mode_bold()       { write("\{BOLD\}"); }
void mode_underscore() { write("\{UNDERSCORED\}"); }


void receive(string text)
{
    func _cb;
    
    for (int i = 0; i < strlen(text); i++)
    {
        if (text[i] == '\n') {
            string line2 = line;
            line = "";
            _cb = callback;
    	    if (_cb != nil) {
        	    callback = nil;
                call(_cb, trim(line2));
            } else {
                log("The system can't provide an input handler -- TODO: Reset to failsafe!\n");
            }
        } else {
            line = line + chr(text[i]);
        }
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
	line = "";
	$on_receive(port, this::receive);
	$on_disconnect(port, this::handle_disconnect);
}
