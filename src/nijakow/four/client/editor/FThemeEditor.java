package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class FThemeEditor extends JDialog {
    private final WritableTheme current;
    //private final JCheckBox bold;
    //private final JCheckBox italic;
    //private final JCheckBox strike;
    //private final JCheckBox underline;
    private final JCheckBox inherit;
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
    private final JLabel desc;
    private final JLabel famDesc;
    private final JLabel bidiDesc;
    private final JLabel foreDesc;
    private final JLabel backDesc;
    private final JLabel sizeDesc;
    private final JLabel flDesc;
    private final JTextField bidi;
    private final JTextField size;
    private final JTextField fam;
    private final JTextField fl;
    private boolean dark;
    private FStyle currentStyle;

    public FThemeEditor(Frame parent, FTheme current) {
        this(parent, current, "New...");
    }

    public FThemeEditor(Frame parent, FTheme current, String name) {
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
        editAll = new JPanel(new BorderLayout());
        cBox = new JPanel(new GridLayout(2, 1));
        desc = new JLabel("The token type to modify the values for:");
        cBox.add(desc);
        JComboBox<TokenType> tokens = new JComboBox<>(TokenType.values());
        tokens.setEditable(false);
        cBox.add(tokens);
        editAll.add(cBox, BorderLayout.NORTH);
        both = new JPanel(new GridLayout(1, 2));
        checkPanel = new JPanel(new GridLayout(7, 1));

        JPanel boldWD = new JPanel(new GridLayout(2, 1));
        boldWD.setBorder(new EtchedBorder());
        JPanel bolds = new JPanel();
        JLabel boldDesc = new JLabel("Bold:");
        JRadioButton boldDefault = new JRadioButton("Default");
        JRadioButton boldDisable = new JRadioButton("Off");
        JRadioButton boldEnable = new JRadioButton("On");
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

        JPanel italicWD = new JPanel(new GridLayout(2, 1));
        italicWD.setBorder(new EtchedBorder());
        JPanel italics = new JPanel();
        JLabel italicDesc = new JLabel("Italic:");
        JRadioButton italicDefault = new JRadioButton("Default");
        JRadioButton italicDisable = new JRadioButton("Off");
        JRadioButton italicEnable = new JRadioButton("On");
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

        JPanel underlineWD = new JPanel(new GridLayout(2, 1));
        underlineWD.setBorder(new EtchedBorder());
        JPanel underlines = new JPanel();
        JLabel underlineDesc = new JLabel("Underlined:");
        JRadioButton underlineDefault = new JRadioButton("Default");
        JRadioButton underlineDisable = new JRadioButton("Off");
        JRadioButton underlineEnable = new JRadioButton("On");
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

        JPanel strikeWD = new JPanel(new GridLayout(2, 1));
        strikeWD.setBorder(new EtchedBorder());
        JPanel strikes = new JPanel();
        JLabel strikeDesc = new JLabel("Strike-through:");
        JRadioButton strikeDefault = new JRadioButton("Default");
        JRadioButton strikeDisable = new JRadioButton("Off");
        JRadioButton strikeEnable = new JRadioButton("On");
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

        bidiPanel = new JPanel(new GridLayout(2, 1));
        bidiPanel.setBorder(new EtchedBorder());
        bidiDesc = new JLabel("The bidilevel:");
        bidi = new JTextField();
        bidiPanel.add(bidiDesc);
        bidiPanel.add(bidi);
        checkPanel.add(bidiPanel);
        sizePanel = new JPanel(new GridLayout(2, 1));
        sizePanel.setBorder(new EtchedBorder());
        sizeDesc = new JLabel("The font size:");
        size = new JTextField();
        sizePanel.add(sizeDesc);
        sizePanel.add(size);
        checkPanel.add(sizePanel);
        flPanel = new JPanel(new GridLayout(2, 1));
        flPanel.setBorder(new EtchedBorder());
        flDesc = new JLabel("The first-line indent:");
        fl = new JTextField();
        flPanel.add(flDesc);
        flPanel.add(fl);
        checkPanel.add(flPanel);
        checkPanel.add(boldWD);
        checkPanel.add(italicWD);
        checkPanel.add(strikeWD);
        checkPanel.add(underlineWD);
        otherPanel = new JPanel(new GridLayout(4, 1));
        inheritBox = new JPanel(new GridLayout(2, 1));
        inheritBox.setBorder(new EtchedBorder());
        inherit = new JCheckBox("Inherit from:");
        JComboBox<TokenType> inTokens = new JComboBox<>(TokenType.values());
        inTokens.setEditable(false);
        inherit.addItemListener(event -> inTokens.setEnabled(inherit.isSelected()));
        inTokens.setEnabled(false);
        inheritBox.add(inherit);
        inheritBox.add(inTokens);
        otherPanel.add(inheritBox);
        famPanel = new JPanel(new GridLayout(2, 1));
        famPanel.setBorder(new EtchedBorder());
        famDesc = new JLabel("Enter the font family:");
        fam = new JTextField("");
        famPanel.add(famDesc);
        famPanel.add(fam);
        otherPanel.add(famPanel);
        backPanel = new JPanel(new GridLayout(2, 1));
        backPanel.setBorder(new EtchedBorder());
        backDesc = new JLabel("The background colour:");
        JPanel back = new JPanel();
        back.setBorder(new EtchedBorder());
        backPanel.add(backDesc);
        backPanel.add(back);
        otherPanel.add(backPanel);
        forePanel = new JPanel(new GridLayout(2, 1));
        forePanel.setBorder(new EtchedBorder());
        foreDesc = new JLabel("The text colour:");
        JPanel fore = new JPanel();
        fore.setBorder(new EtchedBorder());
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
                Color tmp = JColorChooser.showDialog(FThemeEditor.this, "Text colour", currentStyle == null ? null : currentStyle.getBackground());
                if (tmp != null) {
                    if (currentStyle != null) currentStyle.setBackground(tmp);
                    back.setBackground(tmp);
                }
            }
        });
        forePanel.add(foreDesc);
        forePanel.add(fore);
        otherPanel.add(forePanel);
        both.add(otherPanel);
        both.add(checkPanel);
        editAll.add(both, BorderLayout.CENTER);
        saveButtons = new JPanel(new GridLayout(1, 2));
        JButton save = new JButton("Save");
        save.addActionListener(event -> saveToFile(false, name));
        JButton saveAs = new JButton("Save as...");
        saveAs.addActionListener(event -> saveToFile(true, null));
        tokens.addItemListener(event -> {
            // TODO Save changes that have been made!!!
            currentStyle = this.current.getStyle((TokenType) tokens.getSelectedItem());
            // TODO Add default style here!
            if (currentStyle != null) {
                Style s = currentStyle.asStyle(null);
                if (currentStyle.isBoldOverwritten()) (currentStyle.isBold() ? boldEnable : boldDisable).setSelected(true);
                else boldDefault.setSelected(true);
                if (currentStyle.isItalicOverwritten()) (currentStyle.isItalic() ? italicEnable : italicDisable).setSelected(true);
                else italicDefault.setSelected(true);
                if (currentStyle.isUnderlinedOverwritten()) (currentStyle.isUnderlined() ? underlineEnable : underlineDisable).setSelected(true);
                else underlineDefault.setSelected(true);
                if (currentStyle.isStrikeThroughOverwritten()) (currentStyle.isStrikeThrough() ? strikeEnable : strikeDisable).setSelected(true);
                else strikeDefault.setSelected(true);
                fam.setText(StyleConstants.getFontFamily(s));
                size.setText(Integer.toString(StyleConstants.getFontSize(s)));
                bidi.setText(Integer.toString(StyleConstants.getBidiLevel(s)));
                fl.setText(Float.toString(StyleConstants.getFirstLineIndent(s)));
                back.setBackground(StyleConstants.getBackground(s));
                fore.setBackground(StyleConstants.getForeground(s));
                inherit.setSelected(currentStyle.getParent() != null);
                if (currentStyle.getParent() != null) inTokens.setSelectedItem(currentStyle.getParent().getTokenType());
            }
        });
        saveButtons.add(save);
        saveButtons.add(saveAs);
        editAll.add(saveButtons, BorderLayout.SOUTH);
        getContentPane().setLayout(new GridLayout(1, 1));
        getContentPane().add(editAll);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    private void saveToFile(boolean newFile, String fileName) {
        File file = fileName == null ? null : new File(fileName);
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
        }
    }

    @Override
    public void dispose() {
        // TODO Save changes!
        super.dispose();
    }
}
