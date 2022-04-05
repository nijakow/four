inherits "/lib/object.c";
inherits "/lib/io/sprintf.c";

use $write;
use $on_receive;

private any port;

void printf(string format, ...)
{
    $write(port, sprintf(format, ...));
}

private void receive(string line)
{
    printf("%s", line);
}

void _init(any port)
{
    "/lib/object.c"::_init();
    this.port = port;
    $on_receive(port, this::receive);
}
