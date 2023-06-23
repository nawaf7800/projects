
import com.mysql.cj.protocol.Resultset;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.JOptionPane;

/**
 *
 * @author faisa
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button btnok;

    @FXML
    private Label label;

    @FXML
    private PasswordField txtpass;

    @FXML
    private TextField txtuname;

    @FXML
    void login(ActionEvent event) throws IOException {
//        JOptionPane.showMessageDialog(null, "Hi");
    String uname=txtuname.getText();
    String pass=txtpass.getText();
    ResultSet rs;
    
    Connection con;
    PreparedStatement pst;
    
    if(uname.equals("") && pass.equals("")){
        JOptionPane.showMessageDialog(null, "UserName or Password Blank");
    }else{
        try{
            
            
            if(txtuname.getText().equals("root") && txtpass.getText().equals("1234")){
                JOptionPane.showMessageDialog(null, "login Success");
                Parent blah = FXMLLoader.load(getClass().getResource("gameFXML.fxml"));
                Scene scene = new Scene(blah);
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                appStage.setScene(scene);
                appStage.show();
            }else{
                JOptionPane.showMessageDialog(null, "login Failed");
                txtuname.setText("");
                txtpass.setText("");
                txtuname.requestFocus();
            }
        }
        catch(Exception ex){
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE,null, ex);
        }
    }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
