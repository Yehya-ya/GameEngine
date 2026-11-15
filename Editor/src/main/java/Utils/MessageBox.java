package Utils;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class MessageBox {
    private static final int okButton = 1;

    private MessageBox() {}

    public static void showError(@NotNull String title, @NotNull String message) {
        TinyFileDialogs.tinyfd_messageBox(title, message, "ok", "error", okButton);
    }
}
