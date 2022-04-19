package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FThemeEditor extends JDialog {
    private final FTheme current;
    private final JCheckBox bold;
    private final JCheckBox italic;
    private final JCheckBox strike;
    private final JCheckBox underline;
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
        this.current = current;
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
        bold = new JCheckBox("Bold");
        italic = new JCheckBox("Italic");
        strike = new JCheckBox("Strike-through");
        underline = new JCheckBox("Underline");
        bidiPanel = new JPanel(new GridLayout(2, 1));
        bidiDesc = new JLabel("The bidilevel:");
        bidi = new JTextField();
        bidiPanel.add(bidiDesc);
        bidiPanel.add(bidi);
        checkPanel.add(bidiPanel);
        sizePanel = new JPanel(new GridLayout(2, 1));
        sizeDesc = new JLabel("The font size:");
        size = new JTextField();
        sizePanel.add(sizeDesc);
        sizePanel.add(size);
        checkPanel.add(sizePanel);
        flPanel = new JPanel(new GridLayout(2, 1));
        flDesc = new JLabel("The first-line indent:");
        fl = new JTextField();
        flPanel.add(flDesc);
        flPanel.add(fl);
        checkPanel.add(flPanel);
        checkPanel.add(bold);
        checkPanel.add(italic);
        checkPanel.add(strike);
        checkPanel.add(underline);
        otherPanel = new JPanel(new GridLayout(4, 1));
        inheritBox = new JPanel(new GridLayout(2, 1));
        inherit = new JCheckBox("Inherit from:");
        JComboBox<TokenType> inTokens = new JComboBox<>(TokenType.values());
        inTokens.setEditable(false);
        inheritBox.add(inherit);
        inheritBox.add(inTokens);
        otherPanel.add(inheritBox);
        famPanel = new JPanel(new GridLayout(2, 1));
        famDesc = new JLabel("Enter the font family:");
        fam = new JTextField("");
        famPanel.add(famDesc);
        famPanel.add(fam);
        otherPanel.add(famPanel);
        backPanel = new JPanel(new GridLayout(2, 1));
        backDesc = new JLabel("The background colour:");
        JPanel back = new JPanel();
        backPanel.add(backDesc);
        backPanel.add(back);
        otherPanel.add(backPanel);
        forePanel = new JPanel(new GridLayout(2, 1));
        foreDesc = new JLabel("The text colour:");
        JPanel fore = new JPanel();
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
        JButton saveAs = new JButton("Save as...");
        tokens.addItemListener(event -> {
            // TODO Save changes that have been made!!!
            currentStyle = current.getStyle((TokenType) tokens.getSelectedItem());
            if (currentStyle != null) {
                // TODO Add default style here!
                Style s = currentStyle.asStyle(null);
                bold.setSelected(StyleConstants.isBold(s));
                italic.setSelected(StyleConstants.isItalic(s));
                underline.setSelected(StyleConstants.isUnderline(s));
                strike.setSelected(StyleConstants.isStrikeThrough(s));
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Save values in the theme
            }
        });
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
            bold.setForeground(Color.white);
            bold.setBackground(Color.darkGray);
            italic.setForeground(Color.white);
            italic.setBackground(Color.darkGray);
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
            strike.setForeground(Color.white);
            strike.setBackground(Color.darkGray);
            underline.setForeground(Color.white);
            underline.setBackground(Color.darkGray);
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
            bold.setForeground(null);
            bold.setBackground(null);
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
            italic.setForeground(null);
            italic.setBackground(null);
            strike.setForeground(null);
            strike.setBackground(null);
            underline.setForeground(null);
            underline.setBackground(null);
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
