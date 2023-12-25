import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Stack;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DecompressScreen extends BorderPane {
	Label loadFileL = new Label("Choose a file to decompress: ");
	Button loadFileBtn = new Button("Load Compressed File");
	Button saveFileBtn = new Button("Save decompressed File");

	Label originalFileSizeL = new Label("Size before decompression: ");
	Label sizeAfterDecompressionL = new Label("Size after decompression: ");

	static Node root;
	File f;

	public DecompressScreen(Stage primaryStage, Scene scene) {

		GridPane gp = new GridPane();
		gp.add(loadFileL, 0, 0);
		gp.add(loadFileBtn, 1, 0);
		gp.add(saveFileBtn, 1, 1);

		gp.add(originalFileSizeL, 0, 2);
		gp.add(sizeAfterDecompressionL, 0, 3);
		gp.setVgap(15);
		gp.setHgap(15);
		gp.setPadding(new Insets(15));
		setCenter(gp);

		loadFileBtn.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Huffman Coded (*.huff)", "*.huff");
			fc.getExtensionFilters().add(extFilter);
			f = fc.showOpenDialog(primaryStage);
			if (f == null) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText("File Not Found!");
				alert.setContentText("Please choose a file to decompress!");
				alert.show();
				return;
			}
			originalFileSizeL.setText("File size before decompression: " + f.length() + " Bytes");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("File Has Been Loaded!");
			alert.setContentText("File at " + f.getPath() + " has been loaded successfully.");
			alert.show();
		});

		saveFileBtn.setOnAction(e -> {
			try {
				FileChooser fc = new FileChooser();
				DataInputStream reader = new DataInputStream(new FileInputStream(f));
				int extensionLength = reader.readByte();
				int i = 0;
				StringBuilder extension = new StringBuilder("");
				while (reader.available() > 0 && i++ < extensionLength) {
					byte c = reader.readByte();
					extension.append((char) c);
				}
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Original Extension (*."+ extension +")", "*." + extension);
				fc.getExtensionFilters().add(extFilter);
				File outputFile = fc.showSaveDialog(primaryStage);
				DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));

				int seriallizedTreeLength = reader.readInt();
				int j = 0;
				System.out.println("Extension Length: " + extensionLength);
				System.out.println("Extension: " + extension);
				System.out.println("Tree Length: " + seriallizedTreeLength);

				StringBuilder seriallizedTree = new StringBuilder("");
				while (reader.available() > 0 && j++ < seriallizedTreeLength) {
					byte b = reader.readByte();
					char c = (char) b;
					seriallizedTree.append(c);
				}

				System.out.println("Tree:" + seriallizedTree);

				root = reconstructHuffmanTree(seriallizedTree.toString());
				assignCodes(root);
				
				int dataSize = reader.readInt();
				
				System.out.println(dataSize);
				StringBuilder sb = new StringBuilder();
				
				int x = 0;
				while (reader.available() > 0 && x < dataSize) {
					byte b = reader.readByte();				
					String byteString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'); // add back the leading zeros
					int z = 0 ;
		
					while(z < byteString.length()) {
						Byte t = search(sb.toString());
						if(t != null) {
//							System.out.print((char)(byte)t );
							out.write(t);
							sb = new StringBuilder("");
						}
						sb.append(byteString.charAt(z));
						z++;
						x++;
						if(x > dataSize)
							break;
					}
			
				}
				
				sizeAfterDecompressionL.setText("Size after decompression: " + outputFile.length() +" Bytes");
				out.close();
				reader.close();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("File saved successfully!");
				alert.setContentText("The decompressed file has been saved successfully at " + outputFile.getPath());
				alert.show();
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText("File Not Found!");
				alert.setContentText("Please choose a file to decompress!");
				alert.show();

			}
		});

	}

	static Byte b;
	private Byte search(String string) {
		b = null;
		search(root, string);
		return b;
	}
	
	private void search(Node root,String string) {
		if(root == null)
			return;
		
		if(root.getLeft() == null && root.getRight() == null) {
			if(root.getCode().equals(string))
			b = root.getData();
			return;
		}
		search(root.getLeft(),string);
		search(root.getRight(),string);
		
	}

	private Node reconstructHuffmanTree(String string) { // rebuild tree from post order
		Stack<Node> s = new Stack<Node>();
		char[] nodes = string.toCharArray();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == '1') { // a leaf node, push it to the stack
				s.push(new Node((byte) nodes[i + 1]));
				i++;
			} else if (nodes[i] == '0') { // non-leaf node, pop two nodes from the stack and assign the first one to its right and the second one to its left
				if (s.size() == 1) // when there is only one node in the stack that means our tree is done
					break;
				Node n = new Node();
				n.setRight(s.pop());
				n.setLeft(s.pop());
				s.push(n); // push the resulting node back to the stack
			}
		}
		return s.pop();  // pop and return the root of the huffman tree
	}

	public static void assignCodes(Node root) {
		assignCodes(root, "");
	}

	private static void assignCodes(Node root, String code) { 
		if (root == null) {
			return;
		}
		if (root.getLeft() == null && root.getRight() == null) { // leaf nodes
			System.out.println(
					"Byte Value (Signed): " + root.getData() + ", Frequency: " + root.getFreq() + ", Code: " + code);
			root.setCode(code);
		}
		assignCodes(root.getLeft(), code + "0");
		assignCodes(root.getRight(), code + "1");
	}

}
