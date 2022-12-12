package client_gui;

import client.EsClientMain;
import common.Song;
import common.interfaces.SongService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.rmi.RemoteException;
import java.util.List;

public class SearchController {
    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchText;
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableColumn<Song, String> authorColumn;
    @FXML
    private TableColumn<Song, String> albumColumn;
    @FXML
    private TableColumn<Song, String> yearColumn;
    @FXML
    private TableColumn<Song, String> titleColumn;
    @FXML
    private TableColumn<Song, String> genreColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;

    public void initialize() {

        searchBtn.setOnAction(event -> {
            SongService songService = EsClientMain.getSongService();
            String searched = searchText.getText().trim();

            try {
                if (!searched.isEmpty()) {
                    List<Song> results = songService.searchByString(searched);
                    authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
                    albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
                    yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
                    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
                    genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
                    durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
                    songsTable.getItems().addAll(results);


                    for (Song song : results) {
                        System.out.println(song);
                    }
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }
}