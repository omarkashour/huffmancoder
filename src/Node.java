import java.util.Objects;

public class Node implements Comparable<Node> {

	private byte data; // char cant be used because its 2 bytes in java
	private int freq;
	private int codeLength;
	private char asciiChar;
	private String code;
	private Node left;
	private Node right;

	public Node() {

	}

	public int getCodeLength() {
		return codeLength;
	}

	public void setCodeLength(int codeLength) {
		this.codeLength = codeLength;
	}

	public Node(int freq) {

		this.freq = freq;
	}
	public Node(byte data) {

		this.data = data;
		this.setAsciiChar((char) data);
	}
	public Node(byte data, int freq) {

		this.data = data;
		this.freq = freq;
		this.setAsciiChar((char) data);
	}

	public Node(byte data, int freq, String code) {

		this.data = data;
		this.freq = freq;
		this.code = code;
		this.setAsciiChar((char) data);

	}

	public Node(byte data, int freq, Node left, Node right) {
		this.data = data;
		this.freq = freq;
		this.left = left;
		this.right = right;
		this.setAsciiChar((char) data);

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		this.codeLength = code.length();
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
		this.setAsciiChar((char) data);
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "data:" + data + "\nFrequency: " + freq;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		return data == other.data;
	}

	@Override
	public int compareTo(Node o) {
		if (this.freq - o.freq == 0)
			return (char) this.data - (char) o.data;
		return this.freq - o.freq;
	}

	public char getAsciiChar() {
		return asciiChar;
	}

	public void setAsciiChar(char asciiChar) {
		this.asciiChar = asciiChar;
	}

}
