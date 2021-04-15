package de.unibi.agbi.biodwh2.ui;

import de.unibi.agbi.biodwh2.ui.model.SelectableDataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.function.Consumer;

public final class SelectableDataSourceViewCell extends ListCell<SelectableDataSource> {
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private GridPane gridPane;
    private FXMLLoader fxmlLoader;

    private Consumer<SelectableDataSource> activeChangedCallback;

    @Override
    protected void updateItem(final SelectableDataSource dataSource, final boolean empty) {
        super.updateItem(dataSource, empty);
        if (empty || dataSource == null) {
            setText(null);
            setGraphic(null);
            if (gridPane != null)
                gridPane.setOnContextMenuRequested(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(
                        getClass().getClassLoader().getResource("SelectableDataSourceViewCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            idLabel.setText(dataSource.getId());
            idLabel.setTooltip(new Tooltip(dataSource.getId()));
            nameLabel.setText(dataSource.getName());
            nameLabel.setTooltip(new Tooltip(dataSource.getName()));
            activeCheckBox.setSelected(dataSource.isActive());
            activeCheckBox.setOnAction(event -> {
                dataSource.setActive(activeCheckBox.isSelected());
                if (activeChangedCallback != null)
                    activeChangedCallback.accept(dataSource);
            });
            final ContextMenu contextMenu = createContextMenu(dataSource);
            gridPane.setOnContextMenuRequested(
                    event -> contextMenu.show(gridPane, event.getScreenX(), event.getScreenY()));
            gridPane.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
                    showInfoDialog(dataSource);
            });
            setText(null);
            setGraphic(gridPane);
        }
    }

    private ContextMenu createContextMenu(final SelectableDataSource dataSource) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem openItem = new MenuItem("Info");
        openItem.setOnAction(event -> showInfoDialog(dataSource));
        contextMenu.getItems().add(openItem);
        return contextMenu;
    }

    private void showInfoDialog(final SelectableDataSource dataSource) {
        // TODO
    }

    public void setActiveChangedCallback(Consumer<SelectableDataSource> activeChangedCallback) {
        this.activeChangedCallback = activeChangedCallback;
    }
}