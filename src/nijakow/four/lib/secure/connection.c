inherits "/secure/object.c";
inherits "/std/lib/base64/encode.c";
inherits "/std/lib/base64/decode.c";

use $write;
use $close;
use $on_receive;
use $on_disconnect;

private any port;
private func callback;
private func fallback;
private mapping mapped_callbacks;
private string line;
private string escline;
private int id_counter;
private list close_cbs;

void prompt(func cb, string text)
{
    callback = cb;
    write("\{prompt/plain:", base64_encode_string(text), "\}");
}

void password(func cb, string text)
{
    callback = cb;
    write("\{prompt/password:", base64_encode_string(text), "\}");
}

any edit(func cb, string title, string text)
{
    string key = itoa(id_counter++);
    mapped_callbacks[key] = cb;
    write("\{editor/edit:", base64_encode_string(key), ":", base64_encode_string(title), ":", base64_encode_string(text), "\}");
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
        any txt = va_next;
        $log(txt);
        $write(port, txt);
    }
}

void printf(...) { fprintf(this, ...); }

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
    string* tokens = string_split_on_char(ln, ':');
    if (length(tokens) == 0)
        return;
    for (int i = 1; i < length(tokens); i++)
        tokens[i] = base64_decode_string(tokens[i]);
    if (tokens[0] == "editor/saved" && length(tokens) == 3) {
        string key = tokens[1];
        string content = tokens[2];
        func cb = mapped_callbacks[key];
        if (type(cb) == "function") {
            call(mapped_callbacks[key], key, content);
        } else {
            log("The system can't run an escaped event handler!\n");
            write("\n\{RED\}WARNING: The system can't run an escaped event handler!\{RESET\}\n");
        }
    } else if (tokens[0] == "editor/cancelled" && length(tokens) == 2) {
        string key = tokens[1];
        func cb = mapped_callbacks[key];
        if (type(cb) == "function") {
            call(mapped_callbacks[key], key, nil);
        } else {
            log("The system can't run an escaped event handler!\n");
            write("\n\{RED\}WARNING: The system can't run an escaped event handler!\{RESET\}\n");
        }
    } else if (tokens[0] == "file/upload" && length(tokens) == 2) {
        string text = tokens[1];
        log("Received upload command!\n");
        // TODO
    } else {
        log("Undefined escape format:", tokens[0], "!\n");
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

void add_close_cb(func cb)
{
    append(close_cbs, cb);
}

void call_close_cbs()
{
    list cbs = close_cbs;
    close_cbs = {};
    foreach (func f : cbs) {
        call(f);
    }
}

void handle_disconnect()
{
    call_close_cbs();
}

void close()
{
    call_close_cbs();
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
	close_cbs = {};
	$on_receive(port, this::receive);
	$on_disconnect(port, this::handle_disconnect);
}
