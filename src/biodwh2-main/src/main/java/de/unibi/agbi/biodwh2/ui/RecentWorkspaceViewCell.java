package de.unibi.agbi.biodwh2.ui;

import de.unibi.agbi.biodwh2.ui.model.RecentWorkspace;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class RecentWorkspaceViewCell extends ListCell<RecentWorkspace> {
    @FXML
    private Label pathLabel;
    @FXML
    private Label validLabel;
    @FXML
    private GridPane gridPane;
    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(RecentWorkspace workspace, boolean empty) {
        super.updateItem(workspace, empty);
        if (empty || workspace == null) {
            setText(null);
            setGraphic(null);
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
            pathLabel.setText(workspace.path);
            validLabel.setText(workspace.valid ? "" : "Not a valid workspace");
            setText(null);
            setGraphic(gridPane);
        }
    }
}
