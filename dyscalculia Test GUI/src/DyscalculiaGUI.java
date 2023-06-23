
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class DyscalculiaGUI extends Application {

    public static void main(String[] args) {
      launch(args);
        
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Game");
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
                
        stage.show();
    }
    
}
