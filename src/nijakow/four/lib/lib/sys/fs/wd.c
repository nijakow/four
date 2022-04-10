use $statics;

string FileSystem_GetWorkingDirectory()
{
    if ($statics()["pwd"] == nil)
        FileSystem_Chdir("/");
    return $statics()["pwd"];
}

void FileSystem_Chdir(string dir)
{
    $statics()["pwd"] = dir;
}
