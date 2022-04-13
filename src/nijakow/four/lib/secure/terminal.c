inherits "/lib/object.c";
inherits "/lib/io/sprintf.c";
inherits "/lib/base64/encode.c";
inherits "/lib/string/substring.c";
inherits "/lib/base64/encode.c";

use $write;
use $on_receive;
use $close;

private any port;
private func line_callback;
private string line_buffer;
private string escaped_line_buffer;

void printf(string format, ...)
{
    $write(port, sprintf(format, ...));
}

void prompt(func callback, string prompt, ...)
{
    $write(port, "\{prompt/plain:", Base64_Encode(sprintf(prompt, ...)), "\}");
    this.line_callback = callback;
}

void password(func callback, string prompt, ...)
{
    $write(port, "\{prompt/password:", Base64_Encode(sprintf(prompt, ...)), "\}");
    this.line_callback = callback;
}

void open_editor(func callback, string title, string text)
{
    string key = "0";   // TODO
    $write(port, "\{editor/edit:", Base64_Encode(key), ":", Base64_Encode(title), ":", Base64_Encode(text), "\}");
}

void close()
{
    $close(this.port);
}

private void process_escaped(string escaped)
{
    // TODO
}

private void process_line(string line)
{
    func cb;

    if (this.line_callback != nil) {
        cb = this.line_callback;
        this.line_callback = nil;
        call(cb, String_TrimNewline(line));
    }
}

private void process_char(char c)
{
    if (this.escaped_line_buffer != nil) {
        if (c == '\}') {
            process_escaped(this.escaped_line_buffer);
            this.escaped_line_buffer = nil;
        } else {
            this.escaped_line_buffer = this.escaped_line_buffer + Conversion_CharToString(c);
        }
    } else {
        if (c == '\{') {
            this.escaped_line_buffer = "";
        } else if (c == '\n') {
            process_line(this.line_buffer);
            this.line_buffer = "";
        } else {
            this.line_buffer = this.line_buffer + Conversion_CharToString(c);
        }
    }
}

private void receive(string line)
{
    for (int i = 0; i < String_Length(line); i++)
        process_char(line[i]);
}

void _init(any port)
{
    "/lib/object.c"::_init();
    this.port = port;
    this.line_callback = nil;
    this.line_buffer = "";
    this.escaped_line_buffer = nil;
    $on_receive(port, this::receive);
}
