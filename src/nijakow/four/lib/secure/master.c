#include "/lib/object.c"
#include "/lib/io/log.c"

use $on_connect;
use $on_error;
use $statics;

private void logout_func(...)
{
    $statics()["terminal"].printf("Goodbye!\n");
    $statics()["terminal"].close();
}

void receive(any port)
{
	object terminal = new("/secure/terminal.c", port);
	$statics()["terminal"] = terminal;
	new("/secure/logon.c", this::logout_func)->_start();
}

void handle_error(string key, string type, string msg)
{
    System_Log("Received an error!\n");
}

void _init()
{
    "/lib/object.c"::_init();
    $on_error(this::handle_error);
    $on_connect(this::receive);
}
