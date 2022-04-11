inherits "/lib/conversion/char_to_string.c";
inherits "/lib/conversion/int_to_string.c";
inherits "/lib/string/string.c";

use $log;

string sprintf(string format, ...)
{
    string result = "";
    int    length = String_Length(format);
    int    index;
    char   c;

    for (index = 0; index < length; index++)
    {
        if (format[index] != '%')
            result = result + Conversion_CharToString(format[index]);
        else {
            index  = index + 1;
            c      = format[index];
            if (c == 'd')
                result = result + Conversion_IntToString(va_next);
            else if (c == 's')
                result = result + va_next;
            else
                result = result + "%" + Conversion_CharToString(format[index]);
        }
    }

    return result;
}
