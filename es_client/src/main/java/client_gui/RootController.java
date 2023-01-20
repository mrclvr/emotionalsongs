package client_gui;

import client.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class RootController {

    @FXML
    public Button logoutBtn;
    @FXML
    public Button menuPlaylistsBtn;
    @FXML
    private AnchorPane window;
    @FXML
    private Button menuSearchBtn;
    @FXML
    private AnchorPane mainView;

    public void initialize() {

        menuPlaylistsBtn.setDisable(ClientApp.user == null);

        ClientApp.showSongsView(mainView);

        menuSearchBtn.setOnAction(event ->
                ClientApp.showSongsView(mainView)
        );

        menuPlaylistsBtn.setOnAction(event ->
                ClientApp.showPlaylistsView(mainView)
        );

        logoutBtn.setOnAction(event -> {
            ClientApp.user = null;
            ClientApp.currentView = null;
            ClientApp.initLayout("splashScreen");
        });

    }


}