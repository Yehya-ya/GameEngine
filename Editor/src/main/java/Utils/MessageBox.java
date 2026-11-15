package Utils;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class MessageBox {
    private static final int okButton = 1;
    private static final int yesButton = 1;
    private static final int noButton = 2;

    private MessageBox() {}

    public static void showError(@NotNull String title, @NotNull String message) {
        TinyFileDialogs.tinyfd_messageBox(title, message, "ok", "error", okButton);
    }

    public static @NotNull Answer askYesNoCancel(@NotNull String title, @NotNull String question) {
        int pressedButton = TinyFileDialogs.tinyfd_messageBox(title, question, "yesnocancel", "question", yesButton);
        return switch (pressedButton) {
            case yesButton -> Answer.Yes;
            case noButton -> Answer.No;
            default -> Answer.Cancel;
        };
    }

    public enum Answer {
        Yes,
        No,
        Cancel
    }
}
