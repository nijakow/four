use $adduser;
use $deluser;
use $addtogroup;
use $removefromgroup;

string adduser(string name)
{
    return $adduser(name);
}

bool deluser(string name)
{
    return $deluser(name);
}

bool addtogroup(string name, string group)
{
    return $addtogroup(name, group);
}

bool removefromgroup(string name, string group)
{
    return $removefromgroup(name, group);
}
