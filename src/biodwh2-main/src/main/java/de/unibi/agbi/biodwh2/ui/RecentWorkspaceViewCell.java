package de.unibi.agbi.biodwh2.ui;

import de.unibi.agbi.biodwh2.ui.model.RecentWorkspace;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.function.Consumer;

public class RecentWorkspaceViewCell extends ListCell<RecentWorkspace> {
    @FXML
    private Label pathLabel;
    @FXML
    private Label validLabel;
    @FXML
    private GridPane gridPane;
    private FXMLLoader fxmlLoader;

    private Consumer<RecentWorkspace> openCallback;
    private Consumer<RecentWorkspace> removeFromLibraryCallback;
    private Consumer<RecentWorkspace> removeFromFilesystemCallback;
    private Consumer<RecentWorkspace> relocateCallback;

    @Override
    protected void updateItem(final RecentWorkspace workspace, final boolean empty) {
        super.updateItem(workspace, empty);
        if (empty || workspace == null) {
            setText(null);
            setGraphic(null);
            if (gridPane != null)
                gridPane.setOnContextMenuRequested(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("RecentWorkspaceViewCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pathLabel.setText(workspace.getPath());
            validLabel.setText(workspace.isValid() ? "" : "invalid");
            final ContextMenu contextMenu = createContextMenu(workspace);
            gridPane.setOnContextMenuRequested(
                    event -> contextMenu.show(gridPane, event.getScreenX(), event.getScreenY()));
            setText(null);
            setGraphic(gridPane);
        }
    }

    private ContextMenu createContextMenu(final RecentWorkspace workspace) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> {
            if (openCallback != null)
                openCallback.accept(workspace);
        });
        contextMenu.getItems().add(openItem);
        final MenuItem removeFromLibraryItem = new MenuItem("Remove from library");
        removeFromLibraryItem.setOnAction(event -> {
            if (removeFromLibraryCallback != null)
                removeFromLibraryCallback.accept(workspace);
        });
        contextMenu.getItems().add(removeFromLibraryItem);
        final MenuItem removeFromFilesystemItem = new MenuItem("Remove from filesystem");
        removeFromFilesystemItem.setOnAction(event -> {
            if (removeFromFilesystemCallback != null)
                removeFromFilesystemCallback.accept(workspace);
        });
        removeFromFilesystemItem.setDisable(!workspace.isValid());
        contextMenu.getItems().add(removeFromFilesystemItem);
        if (!workspace.isValid()) {
            final MenuItem relocateItem = new MenuItem("Relocate");
            relocateItem.setOnAction(event -> {
                if (relocateCallback != null)
                    relocateCallback.accept(workspace);
            });
            contextMenu.getItems().add(relocateItem);
        }
        return contextMenu;
    }

    public void setOpenCallback(Consumer<RecentWorkspace> openCallback) {
        this.openCallback = openCallback;
    }

    public void setRemoveFromLibraryCallback(Consumer<RecentWorkspace> removeFromLibraryCallback) {
        this.removeFromLibraryCallback = removeFromLibraryCallback;
    }

    public void setRemoveFromFilesystemCallback(Consumer<RecentWorkspace> removeFromFilesystemCallback) {
        this.removeFromFilesystemCallback = removeFromFilesystemCallback;
    }

    public void setRelocateCallback(Consumer<RecentWorkspace> relocateCallback) {
        this.relocateCallback = relocateCallback;
    }
}
