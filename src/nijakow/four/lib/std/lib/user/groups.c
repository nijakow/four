use $findgroup;
use $members;
use $groups;
use $gname;

string findgroup(string name)
{
    return $findgroup(name);
}

list getmembers(string group) { return $members(group); }
list getgroups() { return $groups(); }

string gname(string id)
{
    return $gname(id);
}
