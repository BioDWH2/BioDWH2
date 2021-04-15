package de.unibi.agbi.biodwh2.ui;

import de.unibi.agbi.biodwh2.ui.model.SelectableDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public final class WorkspaceController implements Initializable {
    @FXML
    private ListView<SelectableDataSource> dataSourcesListView;
    private final ObservableList<SelectableDataSource> dataSources;

    public WorkspaceController() {
        dataSources = FXCollections.observableArrayList();
        dataSources.add(new SelectableDataSource("a", "test", true));
        dataSources.add(new SelectableDataSource("b", "test", false));
        dataSources.add(new SelectableDataSource("c", "test", true));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataSourcesListView.setItems(dataSources);
        dataSourcesListView.setCellFactory(this::createDataSourceViewCell);
    }

    private SelectableDataSourceViewCell createDataSourceViewCell(final ListView<SelectableDataSource> listView) {
        final SelectableDataSourceViewCell cell = new SelectableDataSourceViewCell();
        cell.setActiveChangedCallback(this::onDataSourceActiveChanged);
        return cell;
    }

    private void onDataSourceActiveChanged(final SelectableDataSource dataSource) {
        // TODO
    }
}
