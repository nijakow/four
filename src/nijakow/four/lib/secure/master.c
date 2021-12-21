inherit "/secure/stdlib.c";

use $on_connect;

void receive(any connection)
{
	log("New connection!\n");
	object conn_inst = new("/secure/connection.c", connection);
	new("/secure/program.c", conn_inst);
}

void create()
{
	$on_connect(this::receive);
}
