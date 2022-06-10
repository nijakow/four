App subclass: #Apps/Shell
---
run | line
[
    [
        line := Transcript smalltalk: 'Smalltalk > '.
        (line trim = 'exit') ifTrue: [ ^ self ].
        (line isWhitespace) ifFalse: [
            Object handle: [ Transcript store: line compile value; cr   ]
                       do: [ :e |
                               Transcript out: 'Caught an exception:'; cr;
                                          out: '    '; out: e; cr
                           ].
        ].
    ] repeat.
]
