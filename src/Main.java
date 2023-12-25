import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	Font customFontRegular = Font.loadFont(Main.class.getResourceAsStream("/Product Sans Regular.ttf"), 18);
	static Font customFontBold = Font.loadFont(Main.class.getResourceAsStream("/Product Sans Bold.ttf"), 18);

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		TabPane tp = new TabPane();
		Tab compressTab = new Tab("Huffman Coding");
		Tab frequencyTab = new Tab("Frequency Table");
		Tab decompressTab = new Tab("Huffman Decoding");
		compressTab.setClosable(false);
		decompressTab.setClosable(false);
		frequencyTab.setClosable(false);
		tp.getTabs().addAll(compressTab, frequencyTab, decompressTab);
		Scene scene = new Scene(tp, 800, 400);

		CompressScreen cs = new CompressScreen(primaryStage, scene);
		compressTab.setContent(cs);

		FrequencyScreen freqScreen = new FrequencyScreen(primaryStage, scene);
		frequencyTab.setContent(freqScreen);
		
		DecompressScreen dcs = new DecompressScreen(primaryStage, scene);
		decompressTab.setContent(dcs);

		Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
		scene.getStylesheets().add("style.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("Algorithms Project 2  - Omar Kashour - 1210082");
		primaryStage.show();

	}

}
