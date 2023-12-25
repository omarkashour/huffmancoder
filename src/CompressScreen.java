import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import atlantafx.base.theme.PrimerLight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CompressScreen extends BorderPane {
	Label loadFileL = new Label("Choose a file to compress: ");
	static int[] freq = new int[256];
	static String[] huffTable = new String[256];
	Button loadFileBtn = new Button("Choose File");
	Button saveCompressedFileBtn = new Button("Save Compressed File");
	Button displayHeaderBtn = new Button("Display Header");

	Label originalFileSizeL = new Label("Original File Size: ");
	Label sizeAfterCompressionL = new Label("Compressed File Size: ");
	Label reductionRateL = new Label("Size reduction: ");
	Label headerSizeL = new Label("Header Size: ");
	Label headerSizePercentageL = new Label("Header percentage of compressed file: ");
	Label sizeAfterCompressionWithoutHeaderL = new Label("Compressed File Size Without Header:  ");
	Button headerDetailsBtn = new Button("Details");
	
	static Node root;
	static boolean loaded = false;
	static String originalExtension = null;
	File originalFile;
	static int headerSize;
	static double original_size;

	public CompressScreen(Stage primaryStage, Scene scene) {
		for (int i = 0; i < huffTable.length; i++) {
			huffTable[i] = new String("");
		}
		GridPane gp = new GridPane();
		gp.add(loadFileL, 0, 0);
		gp.add(loadFileBtn, 1, 0);
		gp.add(saveCompressedFileBtn, 1, 1);
		gp.add(originalFileSizeL, 0, 2);
		gp.add(sizeAfterCompressionWithoutHeaderL, 0, 3);
		gp.add(sizeAfterCompressionL, 0, 4);
		gp.add(reductionRateL, 0, 5);
		gp.add(headerSizeL, 0, 6);
		gp.add(displayHeaderBtn, 1, 6);
		gp.add(headerDetailsBtn, 2, 6);
		gp.add(headerSizePercentageL, 0, 7);

		gp.setVgap(15);
		gp.setPadding(new Insets(15));
		setCenter(gp);
		
		headerDetailsBtn.setOnAction(e->{
			BorderPane bp = new BorderPane();
			Label titleL = new Label("Header Format Details");
			bp.setTop(titleL);
			Label bodyL = new Label("The header format is: 1 byte for extension length. 1 - 255 bytes for extension, 4 bytes for tree length, (0 - 2^32-1) bytes for tree, 4 bytes for data length.");
			bodyL.setWrapText(true);
			bp.setCenter(bodyL);
			Stage stage = new Stage();
			Scene s = new Scene(bp, 400, 400);
			s.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
			s.getStylesheets().add("style.css");
			stage.setScene(s);
			stage.show();
		});

		loadFileBtn.setOnAction(e -> {
			loaded = false;
			freq = new int[256];
			FileChooser fc = new FileChooser();
			try {
				originalFile = fc.showOpenDialog(primaryStage);
				buildHuffManTree(originalFile);
				loaded = true;
				String path = originalFile.getPath();
				for (int i = path.length() - 1; i > 0; i--) { // find file extension
					if (path.substring(i, path.length() - 1).indexOf(".") > 0) {
						originalExtension = path.substring(i + 2, path.length());
						break;
					}
				}
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText("File Not Found!");
				alert.setContentText("Please choose a file to compress");
				alert.show();
				return;
			}

			headerSize = (seriallizeTree(root).length()) + originalExtension.length() + 4 + 1 + 4;
			headerSizeL.setText("Header Size: " + headerSize + " Bytes");
			double total_compressed_size = 0;
			assignCodes(root);
			total_compressed_size = (Math.ceil(size / 8) + headerSize);
			System.out.println("Size after compression = " + total_compressed_size + " bytes");
			originalFileSizeL.setText("Original size = " + original_size + " bytes");
			sizeAfterCompressionWithoutHeaderL
					.setText("Size after compression Without Header = " + Math.ceil(size / 8) + " Bytes");
			sizeAfterCompressionL
					.setText("Compressed File Size Including Header = " + total_compressed_size + " Bytes");
			reductionRateL
					.setText(String.format("Size reduction = %.2f", (1 - (total_compressed_size / original_size)) * 100)
							+ "% trimmed from original size");
			headerSizePercentageL.setText("Header percentage of compressed file: "
					+ String.format("%.2f", (headerSize / total_compressed_size) * 100) + "%");
			FrequencyScreen.refreshFrequencyTable();

		});

		saveCompressedFileBtn.setOnAction(e -> {
			if (!loaded || originalFile == null) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText("File not loaded!");
				alert.setContentText("Please choose a file to compress first then attempt to save it");
				alert.show();
				return;
			}
			saveAndWriteCompressedFile(primaryStage);
		});

		displayHeaderBtn.setOnAction(e -> {
			if (root == null) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText("File not loaded!");
				alert.setContentText("Please choose a file to compress first then attempt to view its header!");
				alert.show();
				return;
			}
			String seriallizedTree = seriallizeTree(root);
//			int headerSize = (seriallizedTree.length()) + 8;
			String header = originalExtension.length() + "" + originalExtension + "" + seriallizedTree.length() + ""
					+ seriallizedTree;
			Stage stage = new Stage();
			BorderPane bp = new BorderPane();
			TextArea txtArea = new TextArea();
			txtArea.setWrapText(true);
			txtArea.setText(header);
			txtArea.setEditable(false);
			bp.setCenter(txtArea);
			Label titleL = new Label("Huffman File Header\n");
			bp.setTop(titleL);
			titleL.setAlignment(Pos.TOP_CENTER);
			Scene s = new Scene(bp, 400, 400);
			s.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
			s.getStylesheets().add("style.css");
			bp.setMargin(txtArea, new Insets(15));
			bp.setAlignment(titleL, Pos.CENTER);
			stage.setScene(s);
			stage.show();
		});

	}

	public void saveAndWriteCompressedFile(Stage primaryStage) {
		FileChooser fc = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Huffman Coded (*.huff)", "*.huff");
		fc.getExtensionFilters().add(extFilter);
		File f = fc.showSaveDialog(primaryStage);
		try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(originalFile));
				DataOutputStream writer = new DataOutputStream(new FileOutputStream(f))) { // try with

			String seriallizedTree = seriallizeTree(root);
			writer.write((byte) originalExtension.length()); // 1 byte
			for (char c : originalExtension.toCharArray()) { // maximum of 255 bytes
				writer.write((byte) c);
			}
			int length = (int) Math.ceil(seriallizedTree.length());
			writer.writeInt(length); // 4 bytes
			for (char c : seriallizedTree.toCharArray()) { // one byte for each character
				writer.write((byte) c);
			}
			int dataSize = 0;
			for(int i = 0 ; i < freq.length ; i++) {
				if(freq[i] != 0 && huffTable.length != 0)
				dataSize += freq[i] * huffTable[i].length();
			}
			
			System.out.println("Data size = " + dataSize + " bits");
			writer.writeInt(dataSize); // 4 bytes
			
			int bitBuffer = 0; // to accumulate bits
			int bitCount = 0; // to track the number of bits accumulated
			while (reader.available() > 0) {
				byte[] arr = reader.readNBytes(8); // read 8 bytes (64 bits) at a time for more efficiency
				for (int i = 0; i < arr.length; i++) {
					int index = arr[i] & 0xFF; // convert byte to unsigned integer for index
					String code = huffTable[index];

					for (char c : code.toCharArray()) {
						bitBuffer <<= 1; // left shift to make space for the new bit (left shift by one)
						bitBuffer |= (c == '1') ? 1 : 0; // Set the LSB (the right most bit), same as bitBuffer =
															// bitBuffer | (c == '1') ? 1 : 0
						bitCount++;

						if (bitCount == 8) {
//							System.out.println(Integer.toBinaryString((byte) bitBuffer & 0xFF));
							writer.write((byte) bitBuffer);
							bitBuffer = 0; // reset buffer
							bitCount = 0; // reset count
						}
					}
				}
			}

			// If there are remaining bits in the buffer, write them padded with zeros
			if (bitCount > 0) {
				bitBuffer <<= (8 - bitCount); // pad the remaining bits with zeros ( left shifts by the amount of
												// needed bits to complete the byte)
				writer.write((byte) bitBuffer);
			}
			System.out.println("Padded zeros = " + (8 - bitCount));
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("File saved successfully!");
			alert.setContentText("The compressed file has been saved successfully at " + f.getPath());
			alert.show();
		} catch (Exception e1) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("File Not Found!");
			alert.setContentText("Please choose a file to save to!");
			alert.show();
		}

	}

	public void buildHuffManTree(File f) {
		original_size = f.length();
		int count = 0;
		try {
			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(f));
			while (reader.available() > 0) {
				byte[] arr = reader.readNBytes(8);
				for (byte b : arr) { // count the frequency of each byte
					int index = b & 0xFF; // Convert byte to unsigned integer
					if (freq[index] == 0)
						count++;
					freq[index]++; // Increment frequency at the corresponding index
				}
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		MinHeap<Node> h = new MinHeap<Node>(count);
		for (int i = 0; i < freq.length; i++) { // add the the leaves to the min heap
			if (freq[i] > 0) {
				h.add(new Node((byte) i, freq[i]));

			}
		}

		while (h.getSize() > 1) { // create the huffman tree
			Node a = h.removeMin();
			Node b = h.removeMin();
			Node root = new Node(a.getFreq() + b.getFreq());
			root.setLeft(a);
			root.setRight(b);
			h.add(root);
		}
		root = h.removeMin(); // huffman tree root
	}

	static double size = 0;

	public static void assignCodes(Node root) {
		size = 0;
		assignCodes(root, "");
	}

	private static void assignCodes(Node root, String code) { // pre order traversal
		if (root == null) {
			return;
		}
		if (root.getLeft() == null && root.getRight() == null) { // leaf nodes
//			System.out.println(
//					"Byte Value (Signed): " + root.getData() + ", Frequency: " + root.getFreq() + ", Code: " + code);
			size += root.getFreq() * code.length();
			root.setCode(code);
			int index = root.getData() & 0xFF;
			huffTable[index] = code;
		}
		assignCodes(root.getLeft(), code + "0");
		assignCodes(root.getRight(), code + "1");
	}

	static StringBuilder s = new StringBuilder();

	public static String seriallizeTree(Node root) {
		s = new StringBuilder();
		seriallizeTreeHelper(root);
		return s.toString();
	}

	public static void seriallizeTreeHelper(Node root) { // post order
		if (root == null)
			return;
		seriallizeTreeHelper(root.getLeft());
		seriallizeTreeHelper(root.getRight());
		if (root.getLeft() == null && root.getRight() == null) {
			s.append("1" + (char) root.getData()); // leaf node
		} else {
			s.append("0"); // none leaf node
		}
	}

}
