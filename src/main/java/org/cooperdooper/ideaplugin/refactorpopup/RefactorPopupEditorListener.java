package org.cooperdooper.ideaplugin.refactorpopup;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.event.*;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

public class RefactorPopupEditorListener implements EditorFactoryListener {

    private static final String ACTION_ID = "CooperDooper.RefactorPopup.Action";

    private static final int MODIFIER_KEYS = MouseEvent.BUTTON1_DOWN_MASK - 1;

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        event.getEditor().addEditorMouseListener(new PopupTriggerListener());
    }

    /**
     * Listens for the trigger that normally brings up the editor context menu.
     * This is because the trigger is handled before shortcuts, so it consumes
     * the event.  By planting this listener before the trigger listener gets
     * added,  we get to intercept it, and have to bring up our action manually.
     */
    private static class PopupTriggerListener implements EditorMouseListener {

        @Override
        public void mousePressed(EditorMouseEvent eme) {
            MouseEvent mouseEvent = eme.getMouseEvent();
            if (mouseEvent.getButton() == 0) {
                // This is a simulated button click caused by mapping a mouse action to a key action.
                // It's not ours.
                return;
            }
            // Is this the case that would trigger the editor popup?
            if (mouseEvent.isPopupTrigger() && eme.getArea() == EditorMouseEventArea.EDITING_AREA) {
                AnAction action = ActionManager.getInstance().getAction(ACTION_ID);
                if (action instanceof RefactorPopupAction && isEventInShortcuts(mouseEvent, action)) {
                    eme.consume();
                    ((RefactorPopupAction)action).show(mouseEvent, ActionPlaces.MAIN_MENU);
                }
            }
        }

        // See whether the mouse event is one of the action's mouse shortcuts.
        private boolean isEventInShortcuts(MouseEvent mouseEvent, AnAction action) {
            // For some reason, IDEA does not seem to mask these off, but it works!
            int eventMods = (mouseEvent.getModifiersEx() & MODIFIER_KEYS);
            MouseShortcut shortcutEvent = new MouseShortcut(mouseEvent.getButton(), eventMods, mouseEvent.getClickCount());
            Shortcut[] set = action.getShortcutSet().getShortcuts();
            for (Shortcut shortcut : set) {
                if (shortcutEvent.equals(shortcut)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void mouseReleased(@NotNull EditorMouseEvent eme) {
            mousePressed(eme);
        }

        @Override
        public void mouseClicked(@NotNull EditorMouseEvent eme) {
            mousePressed(eme);
        }
    }
}