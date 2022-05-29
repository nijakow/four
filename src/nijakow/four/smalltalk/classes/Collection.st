Object subclass: #Collection;
instanceVariableNames: ''
---
init
[
]
---
storeOn: w
[
    w out: '#('.
    self do: [ :v | w out: v; out: ' ' ].
    w out: ')'.
]
