package de.unibi.agbi.biodwh2.ui;

import de.unibi.agbi.biodwh2.ui.model.RecentWorkspace;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public final class Gui extends Application {
    private static final String IS_DARK_MODE_ENABLED_KEY = "dark_mode";

    private Preferences preferences;
    private final ClassLoader loader = getClass().getClassLoader();
    private boolean isDarkModeEnabled;
    private Scene scene;
    private AnchorPane root;

    public Gui() {
        preferences = Preferences.userRoot().node(this.getClass().getName());
        isDarkModeEnabled = preferences.getBoolean(IS_DARK_MODE_ENABLED_KEY, false);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        root = FXMLLoader.load(loader.getResource("Main.fxml"));
        primaryStage.setTitle("BioDWH2 UI");
        primaryStage.getIcons().add(new Image(loader.getResourceAsStream("icon.png")));
        primaryStage.setResizable(false);
        scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        setDarkModeEnabled(isDarkModeEnabled);
        primaryStage.show();
        showWorkspaceSelection(root);
    }

    private void showWorkspaceSelection(final AnchorPane root) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(loader.getResource("WorkspaceSelection.fxml"));
        final AnchorPane workspaceSelectionRoot = fxmlLoader.load();
        root.getChildren().clear();
        AnchorPane.setTopAnchor(workspaceSelectionRoot, 0.0);
        AnchorPane.setLeftAnchor(workspaceSelectionRoot, 0.0);
        AnchorPane.setBottomAnchor(workspaceSelectionRoot, 0.0);
        AnchorPane.setRightAnchor(workspaceSelectionRoot, 0.0);
        final WorkspaceSelectionController controller = fxmlLoader.getController();
        controller.setDarkModeEnabled(isDarkModeEnabled);
        controller.setToggleDarkModeCallback(this::setDarkModeEnabled);
        controller.setOpenWorkspaceCallback(this::tryOpenWorkspace);
        root.getChildren().add(workspaceSelectionRoot);
    }

    private void setDarkModeEnabled(final boolean darkModeEnabled) {
        isDarkModeEnabled = darkModeEnabled;
        preferences.putBoolean(IS_DARK_MODE_ENABLED_KEY, isDarkModeEnabled);
        if (isDarkModeEnabled) {
            scene.getStylesheets().remove("light-theme.css");
            scene.getStylesheets().add("dark-theme.css");
        } else {
            scene.getStylesheets().remove("dark-theme.css");
            scene.getStylesheets().add("light-theme.css");
        }
    }

    private void tryOpenWorkspace(final RecentWorkspace workspace) {
        if (!workspace.isValidForced())
            return;
        try {
            openWorkspace(workspace);
        } catch (IOException e) {
            Dialogs.showExceptionDialog(e);
        }
    }

    private void openWorkspace(final RecentWorkspace workspace) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(loader.getResource("Workspace.fxml"));
        final AnchorPane workspaceRoot = fxmlLoader.load();
        root.getChildren().clear();
        AnchorPane.setTopAnchor(workspaceRoot, 0.0);
        AnchorPane.setLeftAnchor(workspaceRoot, 0.0);
        AnchorPane.setBottomAnchor(workspaceRoot, 0.0);
        AnchorPane.setRightAnchor(workspaceRoot, 0.0);
        root.getChildren().add(workspaceRoot);
    }
}
