use $adduser;
use $deluser;
use $addtogroup;
use $removefromgroup;

bool System_AddUser(string name)
{
    return $adduser(name) != nil;
}

bool System_DeleteUser(string name)
{
    return $deluser(name);
}

bool System_AddUserToGroup(string user, string group)
{
    return $addtogroup(user, group);
}

bool System_RemoveUserFromGroup(string user, string group)
{
    return $removefromgroup(user, group);
}
