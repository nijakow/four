Apps/Browser/AbstractObjectBrowser subclass: #Apps/Browser/MethodBrowser;
instanceVariableNames: 'methodName'
<<<
run
[
    Transcript cr;
        out: 'The method '; store: self target name; out: ' in class '; out: self target holdingClass; cr.
    UI/Menu new;
        entry: 'Read Documentation' does: [ Transcript out: 'Documentation:'; cr; cr; out: self target doc; cr. ];
        entry: 'Edit...' does: [ self target edit ];
        entry: 'Delete (!!!)' does: [
            Transcript confirm: 'Delete method? ';
                ifTrue: [ self target holdingClass removeMethod: self target name ]
        ];
        entry: 'Exit' does: [];
    run
]
