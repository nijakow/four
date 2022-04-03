use $groups;
use $findgroup;
use $members;

string* System_GetGroups()
{
    return $groups();
}

bool Group_Exists(string name)
{
    return $findgroup(name) != nil;
}

string* Group_GetMembers(string name)
{
    return $members(name);
}
