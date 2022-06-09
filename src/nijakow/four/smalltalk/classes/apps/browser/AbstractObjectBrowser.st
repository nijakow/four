App subclass: #Apps/Browser/AbstractObjectBrowser;
instanceVariableNames: 'target'
<<<
target
[
  ^ target
]
---
target: t
[
    target := t
]
---
Apps/Browser/AbstractObjectBrowser class
<<<
browse: class
[
  ^ (self new); target: class; run
]
