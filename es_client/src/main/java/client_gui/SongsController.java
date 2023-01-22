package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Song;
import common.User;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.List;

public class SongsController {
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
    @FXML
    private TableColumn<Song, Void> emotionViewColumn;


    public void initialize() {

        SongDAO songDAO = ClientApp.getSongDAO();

        searchBtn.setOnAction(event -> {
            String searched = searchText.getText().trim();

            try {
                if (!searched.isEmpty()) {
                    List<Song> results = songDAO.searchByString(searched);
                    authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
                    albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
                    yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
                    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
                    genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
                    durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

                    addEmotionsBtns();

                    songsTable.getItems().clear();
                    songsTable.getItems().addAll(results);

                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addEmotionsBtns() {
        ClientContext context = ClientContext.getInstance();

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {
                    final HBox btnContainer;

                    private final Button viewBtn = new Button("Vedi");
                    private final Button insertBtn = new Button("Inserisci");

                    {
                        viewBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            ClientApp.createStage("songInfoView.fxml", "Info canzone", true);
                        });

                        insertBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            ClientApp.createStage("songInfoView.fxml", "Info canzone", true);
                        });

                        User user = context.getUser();

                        if (user != null) {
                            btnContainer = new HBox(10, viewBtn, insertBtn);
                        } else {
                            btnContainer = new HBox(viewBtn);
                        }
                        btnContainer.setAlignment(Pos.CENTER);
                    }


                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnContainer);
                        }
                    }
                };

        emotionViewColumn.setCellFactory(cellFactory);
    }

}
