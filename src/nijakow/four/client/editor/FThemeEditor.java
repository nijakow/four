package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class FThemeEditor extends JDialog {
    private final WritableTheme current;
    private final FStyle defaultStyle;
    private final JCheckBox inherit;
    private final JComboBox<TokenType> inTokens;
    private final JPanel checkPanel;
    private final JPanel otherPanel;
    private final JPanel editAll;
    private final JPanel both;
    private final JPanel cBox;
    private final JPanel inheritBox;
    private final JPanel saveButtons;
    private final JPanel bidiPanel;
    private final JPanel sizePanel;
    private final JPanel flPanel;
    private final JPanel famPanel;
    private final JPanel forePanel;
    private final JPanel backPanel;
    private final JPanel boldWD;
    private final JPanel bolds;
    private final JPanel italicWD;
    private final JPanel italics;
    private final JPanel strikeWD;
    private final JPanel strikes;
    private final JPanel underlineWD;
    private final JPanel underlines;
    private final JPanel alignmentPanel;
    private final JLabel desc;
    private final JLabel famDesc;
    private final JLabel bidiDesc;
    private final JLabel foreDesc;
    private final JLabel backDesc;
    private final JLabel sizeDesc;
    private final JLabel flDesc;
    private final JLabel boldDesc;
    private final JLabel italicDesc;
    private final JLabel strikeDesc;
    private final JLabel underlineDesc;
    private final JLabel alignmentDesc;
    private final JTextField bidi;
    private final JTextField size;
    private final JTextField fam;
    private final JTextField fl;
    private final JTextField alignment;
    private final JRadioButton boldDefault;
    private final JRadioButton boldEnable;
    private final JRadioButton boldDisable;
    private final JRadioButton italicDefault;
    private final JRadioButton italicEnable;
    private final JRadioButton italicDisable;
    private final JRadioButton underlineDefault;
    private final JRadioButton underlineEnable;
    private final JRadioButton underlineDisable;
    private final JRadioButton strikeDefault;
    private final JRadioButton strikeEnable;
    private final JRadioButton strikeDisable;
    private boolean dark;
    private FStyle currentStyle;
    private String name;

    public FThemeEditor(Frame parent, FTheme current, FStyle defaultStyle) {
        this(parent, current, "New...", defaultStyle);
    }

    public FThemeEditor(Frame parent, FTheme current, String name, FStyle defaultStyle) {
        super(parent, name, true);
        if (current == null) {
            this.current = new WritableTheme();
        } else if (!(current instanceof WritableTheme)) {
            this.current = new WritableTheme();
            for (FStyle style : current.getAllStyles()) {
                this.current.addStyle(style.getTokenType(), style);
            }
        } else {
            this.current = (WritableTheme) current;
        }
        this.defaultStyle = defaultStyle;
        this.name = name;
        editAll = new JPanel(new BorderLayout());
        cBox = new JPanel(new GridLayout(2, 1));
        desc = new JLabel("The token type to modify the values for:");
        cBox.add(desc);
        JComboBox<TokenType> tokens = new JComboBox<>(TokenType.values());
        tokens.setEditable(false);
        cBox.add(tokens);
        editAll.add(cBox, BorderLayout.NORTH);
        both = new JPanel(new GridLayout(1, 2));
        checkPanel = new JPanel(new GridLayout(6, 1));
        boldWD = new JPanel(new GridLayout(2, 1));
        boldWD.setBorder(new EtchedBorder());
        bolds = new JPanel();
        boldDesc = new JLabel("Bold:");
        boldDefault = new JRadioButton("Default");
        boldDisable = new JRadioButton("Off");
        boldEnable = new JRadioButton("On");
        ButtonGroup boldGroup = new ButtonGroup();
        boldGroup.add(boldDefault);
        boldGroup.add(boldDisable);
        boldGroup.add(boldEnable);
        bolds.add(boldDefault);
        bolds.add(boldDisable);
        bolds.add(boldEnable);
        boldWD.add(boldDesc);
        boldWD.add(bolds);
        boldDefault.setSelected(true);
        italicWD = new JPanel(new GridLayout(2, 1));
        italicWD.setBorder(new EtchedBorder());
        italics = new JPanel();
        italicDesc = new JLabel("Italic:");
        italicDefault = new JRadioButton("Default");
        italicDisable = new JRadioButton("Off");
        italicEnable = new JRadioButton("On");
        ButtonGroup italicGroup = new ButtonGroup();
        italicGroup.add(italicDefault);
        italicGroup.add(italicDisable);
        italicGroup.add(italicEnable);
        italics.add(italicDefault);
        italics.add(italicDisable);
        italics.add(italicEnable);
        italicWD.add(italicDesc);
        italicWD.add(italics);
        italicDefault.setSelected(true);
        underlineWD = new JPanel(new GridLayout(2, 1));
        underlineWD.setBorder(new EtchedBorder());
        underlines = new JPanel();
        underlineDesc = new JLabel("Underlined:");
        underlineDefault = new JRadioButton("Default");
        underlineDisable = new JRadioButton("Off");
        underlineEnable = new JRadioButton("On");
        ButtonGroup underlineGroup = new ButtonGroup();
        underlineGroup.add(underlineDefault);
        underlineGroup.add(underlineDisable);
        underlineGroup.add(underlineEnable);
        underlines.add(underlineDefault);
        underlines.add(underlineDisable);
        underlines.add(underlineEnable);
        underlineWD.add(underlineDesc);
        underlineWD.add(underlines);
        underlineDefault.setSelected(true);
        strikeWD = new JPanel(new GridLayout(2, 1));
        strikeWD.setBorder(new EtchedBorder());
        strikes = new JPanel();
        strikeDesc = new JLabel("Strike-through:");
        strikeDefault = new JRadioButton("Default");
        strikeDisable = new JRadioButton("Off");
        strikeEnable = new JRadioButton("On");
        ButtonGroup strikeGroup = new ButtonGroup();
        strikeGroup.add(strikeDefault);
        strikeGroup.add(strikeDisable);
        strikeGroup.add(strikeEnable);
        strikes.add(strikeDefault);
        strikes.add(strikeDisable);
        strikes.add(strikeEnable);
        strikeWD.add(strikeDesc);
        strikeWD.add(strikes);
        strikeDefault.setSelected(true);
        sizePanel = new JPanel(new GridLayout(2, 1));
        sizePanel.setBorder(new EtchedBorder());
        sizeDesc = new JLabel("The font size:");
        size = new JTextField("");
        size.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (size.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                    size.setForeground(dark ? Color.white : Color.black);
                    size.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (size.getText().isEmpty()) {
                    size.setForeground(dark ? Color.darkGray : Color.lightGray);
                    if (currentStyle.getParent() != null && currentStyle.getParent().getSize() != null) {
                        size.setText(Integer.toString(currentStyle.getParent().getSize()));
                    } else {
                        size.setText(Integer.toString(defaultStyle.getSize()));
                    }
                }
            }
        });
        sizePanel.add(sizeDesc);
        sizePanel.add(size);
        checkPanel.add(sizePanel);
        forePanel = new JPanel(new GridLayout(2, 1));
        forePanel.setBorder(new EtchedBorder());
        foreDesc = new JLabel("The text colour:");
        JPanel fore = new JPanel();
        forePanel.add(foreDesc);
        forePanel.add(fore);
        checkPanel.add(forePanel);
        fore.setBorder(new EtchedBorder());
        checkPanel.add(boldWD);
        checkPanel.add(italicWD);
        checkPanel.add(strikeWD);
        checkPanel.add(underlineWD);
        otherPanel = new JPanel(new GridLayout(6, 1));
        inheritBox = new JPanel(new GridLayout(2, 1));
        inheritBox.setBorder(new EtchedBorder());
        inTokens = new JComboBox<>(TokenType.values());
        inTokens.setEditable(false);
        inherit = new JCheckBox("Inherit from:");
        inherit.addItemListener(event -> {
            final boolean selected = inherit.isSelected();
            inTokens.setEnabled(selected);
            if (selected) inTokens.setSelectedIndex(-1);
        });
        inTokens.setEnabled(false);
        inheritBox.add(inherit);
        inheritBox.add(inTokens);
        otherPanel.add(inheritBox);
        backPanel = new JPanel(new GridLayout(2, 1));
        backPanel.setBorder(new EtchedBorder());
        backDesc = new JLabel("The background colour:");
        JPanel back = new JPanel();
        back.setBorder(new EtchedBorder());
        backPanel.add(backDesc);
        backPanel.add(back);
        otherPanel.add(backPanel);
        famPanel = new JPanel(new GridLayout(2, 1));
        famPanel.setBorder(new EtchedBorder());
        famDesc = new JLabel("Enter the font family:");
        fam = new JTextField("");
        fam.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (fam.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                    fam.setForeground(dark ? Color.white : Color.black);
                    fam.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (fam.getText().isEmpty()) {
                    if (currentStyle.getParent() != null && currentStyle.getParent().getFamily() != null) {
                        fam.setText(currentStyle.getParent().getFamily());
                    } else {
                        fam.setText(defaultStyle.getFamily());
                    }
                    fam.setForeground(dark ? Color.darkGray : Color.lightGray);
                }
            }
        });
        famPanel.add(famDesc);
        famPanel.add(fam);
        otherPanel.add(famPanel);
        flPanel = new JPanel(new GridLayout(2, 1));
        flPanel.setBorder(new EtchedBorder());
        flDesc = new JLabel("The first-line indent:");
        fl = new JTextField("");
        fl.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (fl.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                    fl.setForeground(dark ? Color.white : Color.black);
                    fl.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (fl.getText().isEmpty()) {
                    if (currentStyle.getParent() != null && currentStyle.getParent().getFirstLineIndent() != null) {
                        fl.setText(Float.toString(currentStyle.getParent().getFirstLineIndent()));
                    } else {
                        fl.setText(Float.toString(defaultStyle.getFirstLineIndent()));
                    }
                    fl.setForeground(dark ? Color.darkGray : Color.lightGray);
                }
            }
        });
        flPanel.add(flDesc);
        flPanel.add(fl);
        otherPanel.add(flPanel);
        alignmentPanel = new JPanel(new GridLayout(2, 1));
        alignmentDesc = new JLabel("Alignment:");
        alignment = new JTextField("");
        alignment.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (alignment.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                    alignment.setText("");
                    alignment.setForeground(dark ? Color.white : Color.black);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (alignment.getText().isEmpty()) {
                    alignment.setForeground(dark ? Color.darkGray : Color.lightGray);
                    if (currentStyle.getParent() != null && currentStyle.getParent().getAlignment() != null) {
                        alignment.setText(Integer.toString(currentStyle.getParent().getAlignment()));
                    } else {
                        alignment.setText(Integer.toString(defaultStyle.getAlignment()));
                    }
                }
            }
        });
        alignmentPanel.add(alignmentDesc);
        alignmentPanel.add(alignment);
        alignmentPanel.setBorder(new EtchedBorder());
        otherPanel.add(alignmentPanel);
        bidiPanel = new JPanel(new GridLayout(2, 1));
        bidiPanel.setBorder(new EtchedBorder());
        bidiDesc = new JLabel("The bidilevel:");
        bidi = new JTextField("");
        bidi.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (bidi.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                    bidi.setForeground(dark ? Color.white : Color.black);
                    bidi.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (bidi.getText().isEmpty()) {
                    if (currentStyle.getParent() != null && currentStyle.getParent().getBidiLevel() != null) {
                        bidi.setText(Integer.toString(currentStyle.getParent().getBidiLevel()));
                    } else {
                        bidi.setText(Integer.toString(defaultStyle.getBidiLevel()));
                    }
                    bidi.setForeground(dark ? Color.darkGray : Color.lightGray);
                }
            }
        });
        bidiPanel.add(bidiDesc);
        bidiPanel.add(bidi);
        otherPanel.add(bidiPanel);
        fore.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color tmp = JColorChooser.showDialog(FThemeEditor.this, "Text colour", currentStyle == null ? null : currentStyle.getForeground());
                if (tmp != null) {
                    if (currentStyle != null) currentStyle.setForeground(tmp);
                    fore.setBackground(tmp);
                }
            }
        });
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color tmp = JColorChooser.showDialog(FThemeEditor.this, "Background colour", currentStyle == null ? null : currentStyle.getBackground());
                if (tmp != null) {
                    if (currentStyle != null) currentStyle.setBackground(tmp);
                    back.setBackground(tmp);
                }
            }
        });
        both.add(otherPanel);
        both.add(checkPanel);
        editAll.add(both, BorderLayout.CENTER);
        saveButtons = new JPanel(new GridLayout(1, 2));
        JButton save = new JButton("Save");
        save.addActionListener(event -> {
            saveStyle();
            saveToFile(false);
        });
        JButton saveAs = new JButton("Save as...");
        saveAs.addActionListener(event -> {
            saveStyle();
            saveToFile(true);
        });
        inTokens.addItemListener(event -> {
            FStyle newParent = this.current.getStyle((TokenType) inTokens.getSelectedItem());
            if (newParent == null) {
                newParent = new FStyle((TokenType) inTokens.getSelectedItem(), defaultStyle);
                this.current.addStyle(newParent.getTokenType(), newParent);
            }
            currentStyle.setParent(newParent);
            if (fam.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                if (newParent.getFamily() != null) {
                    fam.setText(newParent.getFamily());
                } else {
                    fam.setText(defaultStyle.getFamily());
                }
            }
            if (fl.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                if (newParent.getFirstLineIndent() != null) {
                    fl.setText(Float.toString(newParent.getFirstLineIndent()));
                } else {
                    fl.setText(Float.toString(defaultStyle.getFirstLineIndent()));
                }
            }
            if (size.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                if (newParent.getSize() != null) {
                    size.setText(Integer.toString(newParent.getSize()));
                } else {
                    size.setText(Integer.toString(defaultStyle.getSize()));
                }
            }
            if (alignment.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                if (newParent.getAlignment() != null) {
                    alignment.setText(Integer.toString(newParent.getAlignment()));
                } else {
                    alignment.setText(Integer.toString(defaultStyle.getAlignment()));
                }
            }
            if (bidi.getForeground().equals(dark ? Color.darkGray : Color.lightGray)) {
                if (newParent.getBidiLevel() != null) {
                    bidi.setText(Integer.toString(newParent.getBidiLevel()));
                } else {
                    bidi.setText(Integer.toString(defaultStyle.getBidiLevel()));
                }
            }
            // TODO If the colours have not been overwritten!!!
            if (newParent.getBackground() != null) {
                back.setBackground(newParent.getBackground());
            } else {
                back.setBackground(defaultStyle.getBackground());
            }
            if (newParent.getForeground() != null) {
                fore.setBackground(newParent.getForeground());
            } else {
                fore.setBackground(defaultStyle.getForeground());
            }
        });
        tokens.addItemListener(event -> {
            if (tokens.getSelectedItem() == null) return;
            if (currentStyle != null) saveStyle();
            currentStyle = this.current.getStyle((TokenType) tokens.getSelectedItem());
            if (currentStyle == null) {
                currentStyle = new FStyle((TokenType) tokens.getSelectedItem(), defaultStyle);
                this.current.addStyle((TokenType) tokens.getSelectedItem(), currentStyle);
            }
            if (currentStyle.isBoldOverwritten()) (currentStyle.isBold() ? boldEnable : boldDisable).setSelected(true);
            else boldDefault.setSelected(true);
            if (currentStyle.isItalicOverwritten()) (currentStyle.isItalic() ? italicEnable : italicDisable).setSelected(true);
            else italicDefault.setSelected(true);
            if (currentStyle.isUnderlinedOverwritten()) (currentStyle.isUnderlined() ? underlineEnable : underlineDisable).setSelected(true);
            else underlineDefault.setSelected(true);
            if (currentStyle.isStrikeThroughOverwritten()) (currentStyle.isStrikeThrough() ? strikeEnable : strikeDisable).setSelected(true);
            else strikeDefault.setSelected(true);
            if (currentStyle.isFamilyOverwritten()) {
                fam.setText(currentStyle.getFamily());
                fam.setForeground(dark ? Color.white : Color.black);
            } else {
                if (currentStyle.getParent() != null && currentStyle.getParent().getFamily() != null) {
                    fam.setText(currentStyle.getParent().getFamily());
                } else {
                    fam.setText(defaultStyle.getFamily());
                }
                fam.setForeground(dark ? Color.darkGray : Color.lightGray);
            }
            if (currentStyle.isAlignmentOverwritten()) {
                alignment.setText(Integer.toString(currentStyle.getAlignment()));
                alignment.setForeground(dark ? Color.white : Color.black);
            } else {
                if (currentStyle.getParent() != null && currentStyle.getParent().getAlignment() != null) {
                    alignment.setText(Integer.toString(currentStyle.getParent().getAlignment()));
                } else {
                    alignment.setText(Integer.toString(defaultStyle.getAlignment()));
                }
                alignment.setForeground(dark ? Color.darkGray : Color.lightGray);
            }
            if (currentStyle.isSizeOverwritten()) {
                size.setText(Integer.toString(currentStyle.getSize()));
                size.setForeground(dark ? Color.white : Color.black);
            } else {
                if (currentStyle.getParent() != null && currentStyle.getParent().getSize() != null) {
                    size.setText(Integer.toString(currentStyle.getParent().getSize()));
                } else {
                    size.setText(Integer.toString(defaultStyle.getSize()));
                }
                size.setForeground(dark ? Color.darkGray : Color.lightGray);
            }
            if (currentStyle.isBidiLevelOverwritten()) {
                bidi.setText(Integer.toString(currentStyle.getBidiLevel()));
                bidi.setForeground(dark ? Color.white : Color.black);
            } else {
                if (currentStyle.getParent() != null && currentStyle.getParent().getBidiLevel() != null) {
                    bidi.setText(Integer.toString(currentStyle.getParent().getBidiLevel()));
                } else {
                    bidi.setText(Integer.toString(defaultStyle.getBidiLevel()));
                }
                bidi.setForeground(dark ? Color.darkGray : Color.lightGray);
            }
            if (currentStyle.isFirstLineIndentOverwritten()) {
                fl.setText(Float.toString(currentStyle.getFirstLineIndent()));
                fl.setForeground(dark ? Color.white : Color.black);
            } else {
                if (currentStyle.getParent() != null && currentStyle.getParent().getFirstLineIndent() != null) {
                    fl.setText(Float.toString(currentStyle.getParent().getFirstLineIndent()));
                } else {
                    fl.setText(Float.toString(defaultStyle.getFirstLineIndent()));
                }
                fl.setForeground(dark ? Color.darkGray : Color.lightGray);
            }
            back.setBackground((currentStyle.isBackgroundOverwritten() ? currentStyle : defaultStyle).getBackground());
            fore.setBackground((currentStyle.isForegroundOverwritten() ? currentStyle : defaultStyle).getForeground());
            final FStyle tmpParent = currentStyle.getParent();
            inherit.setSelected(tmpParent != null);
            if (tmpParent != null) inTokens.setSelectedItem(tmpParent.getTokenType());
        });
        tokens.setSelectedIndex(-1);
        saveButtons.add(save);
        saveButtons.add(saveAs);
        editAll.add(saveButtons, BorderLayout.SOUTH);
        getContentPane().setLayout(new GridLayout(1, 1));
        getContentPane().add(editAll);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    private void saveStyle() {
        if (fam.getForeground().equals(dark ? Color.white : Color.black)) currentStyle.setFamily(fam.getText());
        else currentStyle.setFamily(null);
        if (size.getForeground().equals(dark ? Color.white : Color.black)) {
            try {
                int s = Integer.decode(size.getText());
                currentStyle.setSize(s);
            } catch (NumberFormatException e) {
                currentStyle.setSize(null);
            }
        } else currentStyle.setSize(null);
        if (fl.getForeground().equals(dark ? Color.white : Color.black)) {
            try {
                float fli = Float.parseFloat(fl.getText());
                currentStyle.setFirstLineIndent(fli);
            } catch (NumberFormatException e) {
                currentStyle.setFirstLineIndent(null);
            }
        } else currentStyle.setFirstLineIndent(null);
        if (bidi.getForeground().equals(dark ? Color.white : Color.black)) {
            try {
                int bidii = Integer.decode(bidi.getText());
                currentStyle.setBidiLevel(bidii);
            } catch (NumberFormatException e) {
                currentStyle.setBidiLevel(null);
            }
        } else currentStyle.setBidiLevel(null);
        if (alignment.getForeground().equals(dark ? Color.white : Color.black)) {
            try {
                int align = Integer.decode(alignment.getText());
                currentStyle.setAlignment(align);
            } catch (NumberFormatException e) {
                currentStyle.setAlignment(null);
            }
        } else currentStyle.setAlignment(null);
        currentStyle.setBold(boldDefault.isSelected() ? null : boldEnable.isSelected());
        currentStyle.setItalic(italicDefault.isSelected() ? null : italicEnable.isSelected());
        currentStyle.setUnderlined(underlineDefault.isSelected() ? null : underlineEnable.isSelected());
        currentStyle.setStrikeThrough(strikeDefault.isSelected() ? null : strikeEnable.isSelected());
        currentStyle.setParent(inherit.isSelected() ? this.current.getStyle((TokenType) inTokens.getSelectedItem()) : null);
    }

    private void saveToFile(boolean newFile) {
        File file = name == null ? null : new File(name);
        if (file != null && !file.exists() && !newFile) {
            JOptionPane.showMessageDialog(this, "File does not exist (anymore)!\n" +
                    "\nChoose a new one to save.", "File error", JOptionPane.ERROR_MESSAGE);
            newFile = true;
        } else if (file == null && !newFile) newFile = true;
        if (newFile) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDragEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) file = chooser.getSelectedFile();
            else return;
        }
        name = file.getAbsolutePath();
        setTitle(name);
        final File toWrite = file;
        SwingWorker<?, ?> sw = new SwingWorker<Object, Boolean>() {
            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FThemeEditor.this, "Could not save theme to file!\n\n" +
                            e.getLocalizedMessage(), "Could not write file!", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }

            @Override
            protected Boolean doInBackground() throws IOException {
                current.saveToFile(toWrite);
                return true;
            }
        };
        sw.execute();
    }

    public void toggleMode(boolean dark) {
        this.dark = dark;
        if (dark) {
            getContentPane().setBackground(Color.darkGray);
            checkPanel.setBackground(Color.darkGray);
            otherPanel.setBackground(Color.darkGray);
            saveButtons.setBackground(Color.darkGray);
            inheritBox.setBackground(Color.darkGray);
            bidiPanel.setBackground(Color.darkGray);
            sizePanel.setBackground(Color.darkGray);
            flPanel.setBackground(Color.darkGray);
            backPanel.setBackground(Color.darkGray);
            famPanel.setBackground(Color.darkGray);
            forePanel.setBackground(Color.darkGray);
            both.setBackground(Color.darkGray);
            cBox.setBackground(Color.darkGray);
            editAll.setBackground(Color.darkGray);
            foreDesc.setForeground(Color.white);
            foreDesc.setBackground(Color.darkGray);
            backDesc.setForeground(Color.white);
            backDesc.setBackground(Color.darkGray);
            famDesc.setForeground(Color.white);
            famDesc.setBackground(Color.darkGray);
            flDesc.setForeground(Color.white);
            flDesc.setBackground(Color.darkGray);
            bidiDesc.setForeground(Color.white);
            bidiDesc.setBackground(Color.darkGray);
            sizeDesc.setForeground(Color.white);
            sizeDesc.setBackground(Color.darkGray);
            desc.setForeground(Color.white);
            desc.setBackground(Color.darkGray);
            inherit.setForeground(Color.white);
            inherit.setBackground(Color.darkGray);
            fam.setBackground(Color.gray);
            fam.setForeground(Color.white);
            fam.setCaretColor(Color.white);
            fl.setBackground(Color.gray);
            fl.setForeground(Color.white);
            fl.setCaretColor(Color.white);
            bidi.setBackground(Color.gray);
            bidi.setForeground(Color.white);
            bidi.setCaretColor(Color.white);
            size.setBackground(Color.gray);
            size.setForeground(Color.white);
            size.setCaretColor(Color.white);
            underlines.setBackground(Color.darkGray);
            underlineWD.setBackground(Color.darkGray);
            italics.setBackground(Color.darkGray);
            italicWD.setBackground(Color.darkGray);
            bolds.setBackground(Color.darkGray);
            boldWD.setBackground(Color.darkGray);
            strikes.setBackground(Color.darkGray);
            strikeWD.setBackground(Color.darkGray);
            boldDesc.setForeground(Color.white);
            boldDesc.setBackground(Color.darkGray);
            italicDesc.setForeground(Color.white);
            italicDesc.setBackground(Color.darkGray);
            underlineDesc.setForeground(Color.white);
            underlineDesc.setBackground(Color.darkGray);
            strikeDesc.setForeground(Color.white);
            strikeDesc.setBackground(Color.darkGray);
            strikeDefault.setForeground(Color.white);
            strikeDefault.setBackground(Color.darkGray);
            strikeEnable.setForeground(Color.white);
            strikeEnable.setBackground(Color.darkGray);
            strikeDisable.setForeground(Color.white);
            strikeDisable.setBackground(Color.darkGray);
            underlineDefault.setForeground(Color.white);
            underlineDefault.setBackground(Color.darkGray);
            underlineEnable.setForeground(Color.white);
            underlineEnable.setBackground(Color.darkGray);
            underlineDisable.setForeground(Color.white);
            underlineDisable.setBackground(Color.darkGray);
            italicDefault.setForeground(Color.white);
            italicDefault.setBackground(Color.darkGray);
            italicEnable.setForeground(Color.white);
            italicEnable.setBackground(Color.darkGray);
            italicDisable.setForeground(Color.white);
            italicDisable.setBackground(Color.darkGray);
            boldDefault.setForeground(Color.white);
            boldDefault.setBackground(Color.darkGray);
            boldEnable.setForeground(Color.white);
            boldEnable.setBackground(Color.darkGray);
            boldDisable.setForeground(Color.white);
            boldDisable.setBackground(Color.darkGray);
            alignmentPanel.setBackground(Color.darkGray);
            alignmentDesc.setForeground(Color.white);
            alignmentDesc.setBackground(Color.darkGray);
            alignment.setBackground(Color.gray);
            alignment.setCaretColor(Color.white);
            alignment.setForeground(Color.white);
        } else {
            getContentPane().setBackground(null);
            checkPanel.setBackground(null);
            bidiPanel.setBackground(null);
            sizePanel.setBackground(null);
            flPanel.setBackground(null);
            backPanel.setBackground(null);
            famPanel.setBackground(null);
            forePanel.setBackground(null);
            otherPanel.setBackground(null);
            saveButtons.setBackground(null);
            inheritBox.setBackground(null);
            cBox.setBackground(null);
            both.setBackground(null);
            editAll.setBackground(null);
            foreDesc.setForeground(null);
            foreDesc.setBackground(null);
            backDesc.setForeground(null);
            backDesc.setBackground(null);
            famDesc.setForeground(null);
            famDesc.setBackground(null);
            flDesc.setForeground(null);
            flDesc.setBackground(null);
            bidiDesc.setForeground(null);
            bidiDesc.setBackground(null);
            sizeDesc.setForeground(null);
            sizeDesc.setBackground(null);
            desc.setForeground(null);
            desc.setBackground(null);
            inherit.setForeground(null);
            inherit.setBackground(null);
            fam.setBackground(Color.white);
            fam.setForeground(Color.black);
            fam.setCaretColor(Color.black);
            fl.setBackground(Color.white);
            fl.setForeground(Color.black);
            fl.setCaretColor(Color.black);
            size.setBackground(Color.white);
            size.setForeground(Color.black);
            size.setCaretColor(Color.black);
            bidi.setBackground(Color.white);
            bidi.setForeground(Color.black);
            bidi.setCaretColor(Color.black);
            underlines.setBackground(null);
            underlineWD.setBackground(null);
            italics.setBackground(null);
            italicWD.setBackground(null);
            bolds.setBackground(null);
            boldWD.setBackground(null);
            strikes.setBackground(null);
            strikeWD.setBackground(null);
            boldDesc.setForeground(null);
            boldDesc.setBackground(null);
            underlineDesc.setForeground(null);
            underlineDesc.setBackground(null);
            strikeDesc.setForeground(null);
            strikeDesc.setBackground(null);
            italicDesc.setForeground(null);
            italicDesc.setBackground(null);
            strikeDefault.setForeground(null);
            strikeDefault.setBackground(null);
            strikeEnable.setForeground(null);
            strikeEnable.setBackground(null);
            strikeDisable.setForeground(null);
            strikeDisable.setBackground(null);
            underlineDefault.setForeground(null);
            underlineDefault.setBackground(null);
            underlineEnable.setForeground(null);
            underlineEnable.setBackground(null);
            underlineDisable.setForeground(null);
            underlineDisable.setBackground(null);
            italicDefault.setForeground(null);
            italicDefault.setBackground(null);
            italicEnable.setForeground(null);
            italicEnable.setBackground(null);
            italicDisable.setForeground(null);
            italicDisable.setBackground(null);
            boldDefault.setForeground(null);
            boldDefault.setBackground(null);
            boldEnable.setForeground(null);
            boldEnable.setBackground(null);
            boldDisable.setForeground(null);
            boldDisable.setBackground(null);
            alignmentPanel.setBackground(null);
            alignmentDesc.setForeground(null);
            alignmentDesc.setBackground(null);
            alignment.setBackground(Color.white);
            alignment.setCaretColor(Color.black);
            alignment.setForeground(Color.black);
        }
    }

    @Override
    public void dispose() {
        saveStyle();
        super.dispose();
    }
}
