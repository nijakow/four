Port;
category: 'Kernel-IO'
<<<
createIO
[
  ^ (IO new) initWithI: (PortInputStream new init: self)
                     O: (PortOutputStream new init: self)
                  Port: self
]
---
prompt: text
[
  ^ <primitive:port/prompt:>
]
---
password: text
[
  ^ <primitive:port/password:>
]
---
smalltalk: prompt text: text
[
  ^ <primitive:port/smalltalk:text:>
]
---
smalltalk: prompt
[
  ^ self smalltalk: prompt text: ''
]
---
image: image width: w height: h
[
  ^ <primitive:port/image:width:height:>
]
---
charOut: c
[
  ^ <primitive:port/charOut:>
]
---
edit: text title: title
[
  ^ <primitive:port/edit:title:>
]
---
edit
[
  ^ self edit: '' title: 'Something new'
]
---
edit: text title: title onAccept: acceptBlock onCancel: cancelBlock
[
    text := self edit: text title: title.
  ^ (text != nil)  ifTrue: [ acceptBlock value: text ]
                  ifFalse: [ cancelBlock value       ]
]
---
edit: text title: title onAccept: acceptBlock
[
  ^ self edit: text title: title onAccept: acceptBlock onCancel: [ nil ]
]
---
uploadToUser: text
[
  ^ <primitive:port/uploadToUser>
]
---
downloadFromUser
[
  ^ <primitive:port/downloadFromUser>
]
---
close
[
  ^ <primitive:port/close>
]
---
confirm: text | result
[
    [
        result := self prompt: text.
        result := result trim.
        result isEmpty ifFalse: [
                ((result at: 1) = $y)
            or: ((result at: 1) = $Y);
                ifTrue: [ ^ true ].
                ((result at: 1) = $n)
            or: ((result at: 1) = $N);
                ifTrue: [ ^ false ].
        ].
        Transcript out: 'Please answer with [Y]es or [N]o.'; cr.
    ] repeat.
]
