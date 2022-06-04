Class
---
writeOn: w
[
    Symbol instances do: [ :sym |
        (self = sym globalValue) ifTrue: [
            w out: sym name.
          ^ self
        ]
    ].
    super writeOn: w
]
---
edit: name | text
[
    ((self method: name) = nil) ifTrue: [
        text := (name name) + ' | "Local variables"\n[\n  ^ self\n]\n'.
    ] ifFalse: [
        text := (self method: name) source.
    ].
    text := (Transcript smalltalk: ('Method ' + (name name) + ' > ') text: text).
    (text = nil) ifTrue: [ ^ self ].
    (text isWhitespace) ifTrue: [ self removeMethod: name ]
                       ifFalse: [ self addMethod: text    ].
  ^ self
]
