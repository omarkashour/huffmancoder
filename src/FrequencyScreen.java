import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FrequencyScreen extends BorderPane {
	static TableView<Node> tv = new TableView();

	public FrequencyScreen(Stage primaryStage, Scene scene) { // using a tableview

		TableColumn<Node, Byte> byteValueColumn = new TableColumn<>("Byte (signed)");
		byteValueColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

		TableColumn<Node, Character> asciiCharColumn = new TableColumn<>("ASCII Char");
		asciiCharColumn.setCellValueFactory(new PropertyValueFactory<>("asciiChar"));

		TableColumn<Node, Integer> frequencyColumn = new TableColumn<>("Frequency");
		frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("freq"));

		TableColumn<Node, String> codeColumn = new TableColumn<>("Huffman Code");
		codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

		TableColumn<Node, Integer> codeLengthColumn = new TableColumn<>("Code Length (bits)");
		codeLengthColumn.setCellValueFactory(new PropertyValueFactory<>("codeLength"));

		tv.getColumns().addAll(byteValueColumn, asciiCharColumn, frequencyColumn, codeColumn, codeLengthColumn);
		tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv.setPadding(new Insets(10));
		setCenter(tv);
	}

	public static void refreshFrequencyTable() {
		tv.getItems().clear();
		if (CompressScreen.root != null)
			fillTable(CompressScreen.root);
	}

	public static void fillTable(Node root) {
		if (root == null)
			return;

		if (root.getLeft() == null && root.getRight() == null)
			tv.getItems().add(root);
		fillTable(root.getLeft());
		fillTable(root.getRight());

	}
}
