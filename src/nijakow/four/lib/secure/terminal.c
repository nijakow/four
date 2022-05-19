#include "/lib/object.c"
#include "/lib/conversion/int_to_string.c"
#include "/lib/io/sprintf.c"
#include "/lib/string/substring.c"
#include "/lib/string/split.c"
#include "/lib/base64/encode.c"
#include "/lib/base64/decode.c"

use $read;
use $write;
use $write_special;
use $on_receive;
use $on_escape;
use $close;

private any port;
private func line_callback;
private func crash_callback;
private string line_buffer;
private string escaped_line_buffer;
private mapping key_callbacks;
private int key_counter;

void printf(string format, ...)
{
    $write(this.port, sprintf(format, ...));
}

string prompt(string prompt, ...)
{
    $write_special(this.port, "prompt/plain", sprintf(prompt, ...));
    return $read(this.port);
}

string password(string prompt, ...)
{
    $write_special(this.port, "prompt/password", sprintf(prompt, ...));
    return $read(this.port);
}

void open_editor(func callback, string title, string text)
{
    string key = Conversion_IntToString(this.key_counter++);
    this.key_callbacks[key] = callback;
    $write_special(this.port, "editor/edit", key, title, text);
}

void upload(string text)
{
    $write_special(this.port, "file/upload", text);
}

void download(func callback)
{
    string key = Conversion_IntToString(this.key_counter++);
    this.key_callbacks[key] = callback;
    $write_special(this.port, "file/download", key);
}

void close()
{
    $close(this.port);
}

void set_crash_callback(func cb)
{
    if (this.crash_callback == nil) {
        this.crash_callback = cb;
    }
}

private void process_editor(string key, string text, bool cancelled)
{
    func cb;

    cb = this.key_callbacks[key];
    if (cancelled)
        this.key_callbacks[key] = nil;
    if (cb != nil)
        call(cb, text);
}

private void process_upload(string key, string text)
{
    func cb;
    cb = this.key_callbacks[key];
    if (cb != nil) {
        this.key_callbacks[key] = nil;
        call(cb, text);
    } else {
        printf("Uploaded file was discarded.\n");
    }
}

void receive_escaped(string* tokens)
{
    if (tokens[0] == "editor/saved")
        process_editor(tokens[1], tokens[2], false);
    else if (tokens[0] == "editor/cancelled")
        process_editor(tokens[1], nil, true);
    else if (tokens[0] == "file/upload")
        process_upload(tokens[1], tokens[2]);
}

private void handle_crash()
{
    if (this.crash_callback != nil) {
        call(this.crash_callback);
    } else {
        printf("\n");
        printf("The program has crashed and there was no crash callback available.\n");
        printf("This should not happen. Please contact the system administrator.\n");
    }
}

/*
void receive_line(string line)
{
    func cb;

    if (this.line_callback == nil) {
        handle_crash();
        return;
    }
    cb = this.line_callback;
    this.line_callback = nil;
    if (cb != nil)
        call(cb, String_TrimNewline(line));
    if (this.line_callback == nil) {
        handle_crash();
    }
}
*/


void _init(any port)
{
    "/lib/object.c"::_init();
    this.port = port;
    this.crash_callback = nil;
    this.line_callback = nil;
    this.line_buffer = "";
    this.escaped_line_buffer = nil;
    this.key_callbacks = [];
    this.key_counter = 0;
    $on_escape(port, this::receive_escaped);
}
