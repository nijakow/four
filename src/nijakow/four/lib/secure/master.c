#include "/lib/object.c"
#include "/lib/io/log.c"

use $on_connect;
use $on_error;
use $statics;
use $exec;
use $fork;
use $sleep;
use $get_tickables;

private void ticker_loop()
{
    while (true)
    {
        for (any o : $get_tickables())
            o->_tick();
        $sleep(5000);
    }
}

private void ticker()
{
    /*
     * Run the ticker loop in its own thread to
     * avoid crash cascades.
     */
    $exec(this::ticker_loop);
}

private void logout_func()
{
    $statics()["terminal"].printf("Goodbye!\n");
    $statics()["terminal"].close();
}

private void start_logon()
{
    new("/secure/logon.c")->_start();
}

void receive(any port)
{
	object terminal = new("/secure/terminal.c", port);
	$statics()["terminal"] = terminal;
	$exec(this::start_logon);
	logout_func();
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
    $fork(this::ticker);
}
