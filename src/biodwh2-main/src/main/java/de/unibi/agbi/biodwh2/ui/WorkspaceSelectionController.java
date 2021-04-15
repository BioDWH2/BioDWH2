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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public final class WorkspaceSelectionController implements Initializable {
    private final ClassLoader loader = getClass().getClassLoader();
    private static final String RECENT_WORKSPACES_KEY = "recent_workspaces";

    private final Preferences preferences;
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
        preferences = Preferences.userRoot().node(this.getClass().getName());
        recentWorkspaces = FXCollections.observableArrayList();
        final String recent = preferences.get(RECENT_WORKSPACES_KEY, "");
        for (final String path : StringUtils.split(recent, ';'))
            recentWorkspaces.add(new RecentWorkspace(path));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentWorkspaceListView.setItems(recentWorkspaces);
        recentWorkspaceListView.setCellFactory(this::createWorkspaceViewCell);
    }

    private RecentWorkspaceViewCell createWorkspaceViewCell(final ListView<RecentWorkspace> listView) {
        final RecentWorkspaceViewCell cell = new RecentWorkspaceViewCell();
        cell.setOpenCallback(this::onWorkspaceOpen);
        cell.setRemoveFromLibraryCallback(this::onWorkspaceRemoveFromLibrary);
        cell.setRemoveFromFilesystemCallback(this::onWorkspaceRemoveFromFilesystem);
        cell.setRelocateCallback(this::onWorkspaceRelocate);
        return cell;
    }

    private void onWorkspaceOpen(final RecentWorkspace workspace) {
        // TODO
    }

    private void onWorkspaceRemoveFromLibrary(final RecentWorkspace workspace) {
        recentWorkspaces.remove(workspace);
        saveRecentWorkspaces();
    }

    private void saveRecentWorkspaces() {
        preferences.put(RECENT_WORKSPACES_KEY,
                        recentWorkspaces.stream().map(RecentWorkspace::getPath).collect(Collectors.joining(";")));
    }

    private void onWorkspaceRemoveFromFilesystem(final RecentWorkspace workspace) {
        if (Dialogs.showYesNoDialog("Remove",
                                    "This action will remove the workspace from the library and filesystem which is irreversible!",
                                    "Are you sure to remove the workspace from the filesystem?")) {
            recentWorkspaces.remove(workspace);
            saveRecentWorkspaces();
            try {
                final Path path = Paths.get(workspace.getPath());
                //noinspection ResultOfMethodCallIgnored
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (IOException e) {
                Dialogs.showExceptionDialog(e);
            }
        }
    }

    private void onWorkspaceRelocate(final RecentWorkspace workspace) {
        final Path folder = chooseFolder();
        if (folder != null) {
            final int index = recentWorkspaces.indexOf(workspace);
            recentWorkspaces.remove(index);
            recentWorkspaces.add(index, new RecentWorkspace(folder.toString()));
            saveRecentWorkspaces();
        }
    }

    private Path chooseFolder() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final Stage stage = (Stage) darkModeButton.getScene().getWindow();
        final File selectedDirectory = directoryChooser.showDialog(stage);
        return selectedDirectory != null ? selectedDirectory.toPath() : null;
    }

    public void setDarkModeEnabled(final boolean isDarkModeEnabled) {
        darkModeButton.setSelected(isDarkModeEnabled);
        updateDarkMode();
    }

    private void updateDarkMode() {
        final String logoFileName = darkModeButton.isSelected() ? "BioDWH2-logo-dark.png" : "BioDWH2-logo.png";
        final InputStream imageStream = loader.getResourceAsStream(logoFileName);
        if (imageStream != null)
            logo.setImage(new Image(imageStream));
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
        final Path folder = chooseFolder();
        if (folder != null) {
            final RecentWorkspace workspace = new RecentWorkspace(folder.toString());
            if (!recentWorkspaces.contains(workspace)) {
                recentWorkspaces.add(0, workspace);
                saveRecentWorkspaces();
            }
            // TODO: create and open
        }
    }

    @FXML
    private void onClickAdd(final ActionEvent event) {
        event.consume();
        final Path folder = chooseFolder();
        if (folder != null) {
            final RecentWorkspace workspace = new RecentWorkspace(folder.toString());
            if (!recentWorkspaces.contains(workspace)) {
                recentWorkspaces.add(0, workspace);
                saveRecentWorkspaces();
            }
            // TODO: open
        }
    }
}
