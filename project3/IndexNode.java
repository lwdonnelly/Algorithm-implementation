
public class IndexNode {
	IndexNode next;
	int index;
	IndexNode(int index) {
		this.index = index;
	}
	
	IndexNode push(IndexNode newHead) {
		newHead.next = this;
		return newHead;
	}
	
	IndexNode pop() {
		IndexNode newHead = this.next;
		this.next = null;
		return newHead;
	}
}
