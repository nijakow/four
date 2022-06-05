Object subclass: #Image;
instanceVariableNames: 'url width height'
<<<
init
[
    width := -1.
    height := -1.
]
---
url: _url
[
    url := _url
]
---
width: _width
[
    width := _width
]
---
height: _height
[
    height := _height
]
---
writeOn: w
[
    w image: url width: width height: height
]
---
Image class
<<<
newFromURL: url width: width height: height
[
  ^ (self new);
        url: url;
        width: width;
        height: height
]
