inherits "/secure/object.c";

use $write;
use $close;
use $on_receive;
use $on_disconnect;

any port;
func callback;
func fallback;
mapping mapped_callbacks;
string line;
string escline;
int id_counter;

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

any edit(func cb, string title, string text)
{
    string key = itoa(id_counter++);
    mapped_callbacks[key] = cb;
    write("\{$", key, ":", title, ":", text, "\}");
    return key;
}

void set_fallback(func fb)
{
    fallback = fb;
}

void write(...)
{
    while (va_count)
    {
        $write(port, va_next);
    }
}

void printf(string format, ...)
{
    int index = 0;
    int limit = strlen(format);

    while (index < limit)
    {
        if (format[index] != '%') {
            write(chr(format[index]));
        } else {
            index = index + 1;
            if (index >= limit) break;
            else if (format[index] == '%') write("%");
            else if (format[index] == 'a') write(va_next);
            else if (format[index] == 's') write(va_next);
            else if (format[index] == 'd') write(itoa(va_next));
            else if (format[index] == 'x') write(itoax(va_next));
        }
        index = index + 1;
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


void process_escaped(string ln)
{
    int index = 0;
    if (ln[index] == '$') {
        index++;
        int sepIndex = indexof(ln, ':');
        string id;
        string content = nil;
        if (sepIndex < 0)
            id = substr(ln, index, strlen(ln));
        else {
            id = substr(ln, index, sepIndex);
            content = substr(ln, sepIndex + 1, strlen(ln));
        }
        any cb = mapped_callbacks[id];
        if (type(cb) == "function") {
            call(mapped_callbacks[id], id, content);
        } else {
            log("The system can't run an escaped event handler!\n");
            write("\n\{RED\}WARNING: The system can't run an escaped event handler!\{RESET\}\n");
        }
    }
}

void receive(string text)
{
    func _cb;
    
    for (int i = 0; i < strlen(text); i++)
    {
        if (escline != nil) {
            if (text[i] == '\}') {
                process_escaped(escline);
                escline = nil;
            } else {
                escline = escline + chr(text[i]);
            }
        } else if (text[i] == '\{') {
            escline = "";
        } else if (text[i] == '\n') {
            string line2 = line;
            line = "";
            _cb = callback;
    	    if (_cb != nil) {
        	    callback = nil;
                call(_cb, trim(line2));
            } else {
                log("The system can't provide an input handler! Calling fallback...\n");
                mode_normal();
                mode_red();
                mode_italic();
                mode_underscore();
                write("\nWARNING: Fallback handler triggered! Please retype your input.\n");
                mode_normal();
                if (fallback != nil) {
                    call(fallback);
                }
            }
        } else {
            line = line + chr(text[i]);
        }
    }
}

void handle_disconnect()
{
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
	fallback = nil;
	line = "";
	escline = nil;
	mapped_callbacks = [];
	id_counter = 0;
	$on_receive(port, this::receive);
	$on_disconnect(port, this::handle_disconnect);
}
