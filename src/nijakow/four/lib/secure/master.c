inherit "/secure/stdlib.c";

use $on_connect;

void receive(any connection)
{
	any conn_inst;
	log("New connection!\n");
	conn_inst = "/secure/connection.c"->clone(connection);
	"/secure/program.c"->clone(conn_inst, the_object("/prg/chat/chatroom.c"));
}

void create()
{
    "/prg/chat/chatroom.c"->create();
	$on_connect(this::receive);
}
