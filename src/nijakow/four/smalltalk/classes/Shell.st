Object subclass: #Shell
---
run | line
[
    [
        line := Transcript smalltalk: 'Smalltalk > '.
        (line isEmpty) ifFalse: [
            Exception handle: [ Transcript store: line compile value; cr ]
                          do: [ Transcript out: 'Caught an exception!'; cr ].
        ].
    ] repeat.
]
