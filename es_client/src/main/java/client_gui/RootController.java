package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Emotion;
import common.Playlist;
import common.User;
import common.interfaces.EmotionDAO;
import common.interfaces.PlaylistDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.rmi.RemoteException;
import java.util.List;

public class RootController {
    @FXML
    public Button logoutBtn;
    @FXML
    public Button signupBtn;
    @FXML
    public Button loginBtn;
    @FXML
    public Label userLabel;
    @FXML
    public AnchorPane window;
    @FXML
    public ListView<Playlist> playlistsList;
    @FXML
    private Button menuSearchBtn;
    @FXML
    public Button menuPlaylistsBtn;
    @FXML
    private AnchorPane mainView;

    public void initialize() throws RemoteException {
        ClientApp.setMainView(mainView);
        ClientContext context = ClientContext.getInstance();

        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        EmotionDAO emotionDAO = ClientApp.getEmotionDAO();
        List<Emotion> emotions = emotionDAO.getEmotions();

        context.setEmotions(emotions);

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("user")) {

                User newUser = (User) e.getNewValue();

                if (newUser != null) {
                    try {
                        initPlaylistList(playlistDAO, context);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    context.setCurrentPlaylist(null);
                    ClientApp.showSearchView();
                }
                userLabel.setText(newUser != null ? "Ciao, " + newUser.getUsername() : "");
                menuPlaylistsBtn.setDisable(newUser == null);
                loginBtn.setDisable(newUser != null);
                signupBtn.setDisable(newUser != null);
                logoutBtn.setDisable(newUser == null);
                playlistsList.setVisible(newUser != null);
            }
        });

        playlistsList.setVisible(false);

        menuSearchBtn.setOnAction(event ->
                ClientApp.showSearchView()
        );

        menuPlaylistsBtn.setOnAction(event ->
                ClientApp.showPlaylistsView()
        );

        signupBtn.setOnAction(event ->
                ClientApp.createStage("signupView.fxml", "Registrazione utente", true)
        );

        loginBtn.setOnAction(event ->
                ClientApp.createStage("loginView.fxml", "Login", true)
        );

        logoutBtn.setOnAction(event -> {
            final String msg = "Sei sicuro di voler uscire dal tuo account?";
            final boolean res = ClientApp.createAlert(Alert.AlertType.CONFIRMATION, "Conferma", null, msg, true, true);
            if (res) context.setUser(null);

        });
    }

    private void initPlaylistList(PlaylistDAO playlistDAO, ClientContext context) throws RemoteException {
        User user = context.getUser();
        List<Playlist> playlists = playlistDAO.getUserPlaylists(user.getId());

        context.setUserPlaylists(playlists);
        ObservableList<Playlist> userPlaylists = context.getUserPlaylists();

        userPlaylists.addListener((ListChangeListener.Change<? extends Playlist> playlist) ->
                playlistsList.setItems(FXCollections.observableArrayList(userPlaylists))
        );

        playlistsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist playlist, boolean empty) {
                super.updateItem(playlist, empty);
                setText(empty ? "" : playlist.getName());
            }
        });

        playlistsList.prefHeightProperty().bind(Bindings.size(userPlaylists).multiply(36).add(2));

        playlistsList.setItems(FXCollections.observableArrayList(userPlaylists));

        playlistsList.setOnMouseClicked(playlist -> {
            Playlist current = playlistsList.getSelectionModel().getSelectedItem();
            if (current != null) context.setCurrentPlaylist(current);
            ClientApp.showPlaylistsView();
        });
    }
}