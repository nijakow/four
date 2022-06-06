App subclass: #Apps/Shell;
instanceVariableNames: 'running'
---
init
[
    running := false.
]
---
run | line
[
    running := true.
    [ running ] whileTrue: [
        line := Transcript smalltalk: 'Smalltalk > '.
        (line isWhitespace) ifFalse: [
            Object handle: [ Transcript store: line compile value; cr   ]
                       do: [ :e |
                               Transcript out: 'Caught an exception:'; cr;
                                          out: '    '; out: e; cr
                           ].
        ].
    ].
]
---
exit
[
    running := false.
]
