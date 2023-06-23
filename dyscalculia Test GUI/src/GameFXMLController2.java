
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class GameFXMLController2 implements Initializable {

	@FXML
    private ComboBox<String> comboBox;
    @FXML
    private Label miss;
    @FXML
    private ImageView p10;
    @FXML
    private ImageView p11;
    @FXML
    private ImageView p12;
    @FXML
    private ImageView p13;
    @FXML
    private ImageView p14;
    @FXML
    private ImageView p15;
    @FXML
    private ImageView p16;
    @FXML
    private ImageView p17;
    @FXML
    private ImageView p18;
    @FXML
    private ImageView p19;
    @FXML
    private ImageView p20;
    @FXML
    private ImageView p21;
    @FXML
    private ImageView p22;
    @FXML
    private ImageView p23;
    @FXML
    private ImageView p24;
    @FXML
    private ImageView p25;
    @FXML
    private ImageView p26;
    @FXML
    private ImageView p27;
    @FXML
    private ImageView p28;
    @FXML
    private ImageView p29;
    @FXML
    private ImageView p30;
    @FXML
    private ImageView p31;
    @FXML
    private ImageView p32;
    @FXML
    private ImageView p33;
    @FXML
    private ImageView p34;
    @FXML
    private ImageView p35;
    @FXML
    private ImageView p36;
    @FXML
    private ImageView p37;
    @FXML
    private ImageView p38;
    @FXML
    private ImageView p39;
    @FXML
    private ImageView p40;
    @FXML
    private ImageView p41;
    @FXML
    private ImageView p42;
    @FXML
    private ImageView p43;
    @FXML
    private ImageView p44;
    @FXML
    private ImageView p45;
    @FXML
    private ImageView p46;
    @FXML
    private ImageView p47;
    @FXML
    private ImageView p48;
    @FXML
    private ImageView p49;
    @FXML
    private Label score;
    @FXML
    private Button t11;
    @FXML
    private Button t12;
    @FXML
    private Button t13;
    @FXML
    private Button t14;
    @FXML
    private Button t21;
    @FXML
    private Button t22;
    @FXML
    private Button t23;
    @FXML
    private Button t24;
    @FXML
    private Button t31;
    @FXML
    private Button t32;
    @FXML
    private Button t33;
    @FXML
    private Button t34;
    @FXML
    private Button t41;
    @FXML
    private Button t42;
    @FXML
    private Button t43;
    @FXML
    private Button t44;
    
    ArrayList<Button> answerButtons = new ArrayList<Button>();;
    int sc = 0;
    int ms = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
        comboBox.setPromptText("Count the Figures");
        comboBox.getItems().add("Number Comparison");
        comboBox.getItems().add("Count the Figures");
    	
    	ImageView[][] imageViews = {{p11, p12, p13, p14, p15, p16, p17, p18, p19, p10},
    								{p21, p22, p23, p24, p25, p26, p27, p28, p29, p20},
    								{p31, p32, p33, p34, p35, p36, p37, p38, p39, p30},
    								{p41, p42, p43, p44, p45, p46, p47, p48, p49, p40}};
    	
		Random rn = new Random();
		
		File folder = new File("src\\images");
		Image[] images = new Image[4];
		for(int i = 0; i < 4; i++)
			images[i] = new Image(folder.listFiles()[rn.nextInt(folder.listFiles().length)].getAbsolutePath());
		
	    Button[][] buttons = {{t11, t12, t13, t14},
							{t21, t22, t23, t24},
							{t31, t32, t33, t34},
							{t41, t42, t43, t44}};
				    	
		for(int i = 0; i < imageViews.length; i++) {
			
	        ArrayList<Integer> list = new ArrayList<Integer>();
	        for (int j = 1; j < 10; j++) list.add(j);
	        Collections.shuffle(list);
	    	buttons[i][0].setText(Integer.toString(list.get(0)));
	    	buttons[i][1].setText(Integer.toString(list.get(1)));
	    	buttons[i][2].setText(Integer.toString(list.get(2)));
	    	buttons[i][3].setText(Integer.toString(list.get(3)));
	    	int answerPosition = rn.nextInt(4);
	    	answerButtons.add(buttons[i][answerPosition]);
	    	
	    	for(int j = 0; j < list.get(answerPosition); j++) {
	    		imageViews[i][j].setImage(images[i]);
	    	}
		}
    }
    
    @FXML
    void ChangeGame(ActionEvent event) throws IOException {
    	if(comboBox.getValue().equals("Number Comparison")) {
    		Parent blah = FXMLLoader.load(getClass().getResource("gameFXML.fxml"));
            Scene scene = new Scene(blah);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();
    	}
    	else if(comboBox.getValue().equals("Count the Figures")) {
    		Parent blah = FXMLLoader.load(getClass().getResource("gameFXML2.fxml"));
            Scene scene = new Scene(blah);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();
    	}
    }
    
    @FXML
    void t1c(ActionEvent event) {
    	
    	Button[][] buttons = {{t11, t12, t13, t14},
							{t21, t22, t23, t24},
							{t31, t32, t33, t34},
							{t41, t42, t43, t44}};
    	
    	if(answerButtons.contains(event.getSource())) {
    		score.setText(++sc + "");
    	} else {
            miss.setText(++ms + "");
        }
    	for(int i = 0; i < buttons.length; i++) {
    		if(Arrays.asList(buttons[i]).contains(event.getSource())) {
    			buttons[i][0].setDisable(true);
    			buttons[i][1].setDisable(true);
    			buttons[i][2].setDisable(true);
    			buttons[i][3].setDisable(true);
    		}
    	}
    }
}