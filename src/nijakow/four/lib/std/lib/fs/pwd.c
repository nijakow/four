use $statics;

string pwd()
{
    if ($statics()["pwd"] == nil)
        chdir("/");
    return $statics()["pwd"];
}

void chdir(string dir)
{
    $statics()["pwd"] = dir;
}
