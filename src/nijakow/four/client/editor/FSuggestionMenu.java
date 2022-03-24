package nijakow.four.client.editor;

import javax.swing.JPopupMenu;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FSuggestionMenu extends JPopupMenu {
    private final List<FSuggestion> suggestions;
    private int index;

    public FSuggestionMenu() {
        suggestions = new ArrayList<>();
        index = -1;
    }

    public void add(FSuggestion suggestion) {
        suggestions.add(suggestion);
        super.add(suggestion);
    }

    public void addAll(Collection<FSuggestion> suggestions) {
        for (FSuggestion suggestion : suggestions) {
            add(suggestion);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (!b) {
            index = -1;
        }
        super.setVisible(b);
    }

    public void selectNext() {
        if (index >= 0) {
            suggestions.get(index).setBackground(null);
        }
        if (index == suggestions.size() - 1) {
            index = -1;
        }
        suggestions.get(++index).setBackground(Color.blue);
    }

    public void selectPrevious() {
        if (index >= 0) {
            suggestions.get(index).setBackground(null);
        }
        if (index <= 0) {
            index = suggestions.size();
        }
        suggestions.get(--index).setBackground(Color.blue);
    }

    public String getSelection() {
        if (index == -1) {
            return null;
        }
        return suggestions.get(index).getText();
    }

    @Override
    public void removeAll() {
        suggestions.clear();
        super.removeAll();
    }
}
