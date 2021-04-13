package de.unibi.agbi.biodwh2.ui;

import de.unibi.agbi.biodwh2.ui.model.RecentWorkspace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public final class WorkspaceSelectionController implements Initializable {
    private final ClassLoader loader = getClass().getClassLoader();
    @FXML
    private ListView<RecentWorkspace> recentWorkspaceListView;
    private final ObservableList<RecentWorkspace> recentWorkspaces;
    @FXML
    private ToggleButton darkModeButton;
    @FXML
    private ImageView githubButton;
    @FXML
    private ImageView logo;
    private Consumer<Boolean> toggleDarkModeListener;

    public WorkspaceSelectionController() {
        recentWorkspaces = FXCollections.observableArrayList();
        recentWorkspaces.addAll(new RecentWorkspace("C:\\foo\\bar1", true), new RecentWorkspace("C:\\foo\\bar2", true),
                                new RecentWorkspace("C:\\foo\\bar3", false), new RecentWorkspace("C:\\foo\\bar4", true),
                                new RecentWorkspace("C:\\foo\\bar5", true));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentWorkspaceListView.setItems(recentWorkspaces);
        recentWorkspaceListView.setCellFactory(studentListView -> new RecentWorkspaceViewCell());
    }

    public void setDarkModeEnabled(final boolean isDarkModeEnabled) {
        darkModeButton.setSelected(isDarkModeEnabled);
        updateDarkMode();
    }

    private void updateDarkMode() {
        final String logoFileName = darkModeButton.isSelected() ? "BioDWH2-logo-dark.png" : "BioDWH2-logo.png";
        logo.setImage(new Image(loader.getResourceAsStream(logoFileName)));
    }

    public void setToggleDarkModeListener(final Consumer<Boolean> toggleDarkModeListener) {
        this.toggleDarkModeListener = toggleDarkModeListener;
    }

    @FXML
    private void onToggleDarkMode(final ActionEvent event) {
        event.consume();
        if (toggleDarkModeListener != null)
            toggleDarkModeListener.accept(darkModeButton.isSelected());
        updateDarkMode();
    }

    @FXML
    private void onClickGithub(final MouseEvent event) throws URISyntaxException, IOException {
        event.consume();
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
            Desktop.getDesktop().browse(new URI("https://biodwh2.github.io"));
    }

    @FXML
    private void onClickNew(final ActionEvent event) {
        event.consume();
    }

    @FXML
    private void onClickAdd(final ActionEvent event) {
        event.consume();
    }
}
