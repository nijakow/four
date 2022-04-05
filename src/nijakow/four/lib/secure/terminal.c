inherits "/lib/object.c";
inherits "/lib/io/sprintf.c";
inherits "/lib/base64/encode.c";

use $write;
use $on_receive;

private any port;
private func line_callback;

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


private void receive(string line)
{
    func cb;

    if (this.line_callback != nil) {
        cb = this.line_callback;
        this.line_callback = nil;
        call(this.line_callback, line); // TODO: Trim line
    }
}

void _init(any port)
{
    "/lib/object.c"::_init();
    this.port = port;
    this.line_callback = nil;
    $on_receive(port, this::receive);
}
