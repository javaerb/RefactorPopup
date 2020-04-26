package org.cooperdooper.ideaplugin.refactorpopup;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class RefactorPopupAction extends AnAction {

    private static final String GROUP_REFACTOR = "RefactoringMenu";

    private final ActionGroup inner;

    public RefactorPopupAction() {
        inner = (ActionGroup) ActionManager.getInstance().getAction(GROUP_REFACTOR);
        copyFrom(inner);
        setEnabledInModalContext(inner.isEnabledInModalContext());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        inner.update(e);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        InputEvent ie = e.getInputEvent();
        if (ie instanceof MouseEvent) {
            show((MouseEvent)ie, e.getPlace());
        }
    }

    void show(MouseEvent mouseEvent, String place) {
        ActionManager manager = ActionManager.getInstance();
        ActionPopupMenu popup = manager.createActionPopupMenu(place, inner);
        Component invoker = mouseEvent.getComponent();
        Point location = new Point(mouseEvent.getX(), mouseEvent.getY());
        if (invoker instanceof Window) {
            Component focus = ((Window)invoker).getFocusOwner();
            location = SwingUtilities.convertPoint(invoker, location, focus);
            invoker = focus;
        }
        popup.getComponent().show(invoker, location.x,  location.y);
    }
}