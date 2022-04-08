inherits "/lib/object.c";
inherits "/lib/io/log.c";

use $on_connect;
use $on_error;
use $statics;

private void logout_func(...)
{
}

void receive(any port)
{
	System_Log("New connection!\n");
	object terminal = new("/secure/terminal.c", port);
	$statics()["terminal"] = terminal;
	new("/secure/logon.c", nil)->_start();
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
